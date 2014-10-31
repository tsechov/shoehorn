package services.mailer

import java.io.ByteArrayInputStream

import akka.actor.{Props, Actor}
import akka.event.Logging
import models.AssetSupport.IdType
import models.customer.ContactIn

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{Json, JsObject, JsArray}
import akka.pattern.ask
import services.ConfigSupport._

import services.{MailRequest, CrudServiceInternal, OrderCreateMailRequest, OrderPrintServiceInternal}
import services.mailer.order.support.{OrderMailBody, Mail}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Success, Failure, Try}
import scala.concurrent.Future
import services.storage.S3Bucket
import services.mailer.order.support._


object OrderMailer {
  def props(crudService: CrudServiceInternal, printer: OrderPrintServiceInternal): Props = Props(new OrderMailer(crudService, printer))
}

case class OrderMailIngredients(
                                 agentEmail: String,
                                 customerContactId: String,
                                 orderNumber: String,
                                 message: MessageTexts,
                                 req: MailRequest)

case class MessageTexts(text: String,
                        html: String)

class OrderMailer(crudService: CrudServiceInternal, printer: OrderPrintServiceInternal) extends Actor with S3Bucket {
  val mode = configKey("shoehorn.mode", "dev")
  val log = Logging(context.system, this)
  val mailer = context.actorOf(Props[MailSender])

  override def receive = {
    case req: OrderCreateMailRequest => {
      log.debug(s"received OrderCreateMailRequest: $req")

      printer.getPdf(req.orderId).map {
        _.map {
          _.map {
            (reportContainer) =>

              val res = for {
                orderNumber <- (reportContainer.order \ "orderNumber").asOpt[String]
                customerName <- (reportContainer.customer \ "name").asOpt[String]

                contactId <- (reportContainer.customer \ "contactIds").as[JsArray].value.headOption.flatMap(_.asOpt[String])
                agentEmail <- firstMailAddress(reportContainer.agent)
                storedLink <- printer.storePdfInternal(req.storageKey, new ByteArrayInputStream(reportContainer.bytes))

              } yield {
                log.debug(s"order report for mail stored at: $storedLink")

                val mailParams = OrderMailBody(customerName, req.url(bucketName), orderNumber)
                val params = OrderMailIngredients(agentEmail, contactId, orderNumber, createTexts(mailParams), req)

                sendMail(params)
              }
              res.getOrElse(log.error(s"cannot gather mail contents for order: ${req.orderId}"))


          }
        }
      }


    }
  }


  private def sendMail(params: OrderMailIngredients) = {
    implicit val timeout = Timeout(5 seconds)
    crudService.getById[ContactIn](params.customerContactId).map {
      _.map {
        _.map {
          (contact) => {
            firstMailAddress(contact).map {
              (customerMail) => {
                val recipients = mode match {
                  case "prod" => Seq(customerMail, params.agentEmail)
                  case "dev" => Seq("tsechov@gmail.com", "zsoldoszsolt@gmail.com")
                }
                val mail = Mail(recipients, subject = s"SzamosKölyök rendelés [${params.orderNumber}]", message = params.message.text, richMessage = Some(params.message.html))

                val result = (mailer ? mail).mapTo[Try[String]]


                result.onFailure { case t => mailError(t, mail)}
                result.onSuccess {
                  case res: Failure[String] => mailError(res.exception, mail)
                  case res: Success[String] => {
                    log.debug(s"mail sent with id: ${res.get} for order: ${params.req.orderId}")
                    val result = Json.prettyPrint(Json.obj("result" -> "OK", "Message-ID" -> res.get))
                    printer.storePdfInternal(params.req.resultKey, new ByteArrayInputStream(result.getBytes))
                    log.debug(s"mail result stored at: ${params.req.resultUrl(bucketName)} for order: ${params.req.orderId}")
                  }
                }
              }
            }
          }
        }
      }
    }

  }


  private def createTexts(mailParams: OrderMailBody): MessageTexts = {
    val textMessage = orderCreateMailAsText(mailParams)
    val htmlMessage = orderCreateMailAsHtml(mailParams)
    MessageTexts(textMessage, htmlMessage)
  }

  private def mailError(t: Throwable, mail: Mail) = {
    log.error(t, "couldnt send email")
  }

  private def firstMailAddress(from: JsObject): Option[String] = {
    (from \ "emails").as[JsArray].value.map(v => (v \ "address").asOpt[String]).headOption.flatten.flatMap {
      case s: String if s.trim.isEmpty => None
      case s => Some(s)
    }
  }
}
