package services.mailer

import java.io.ByteArrayInputStream

import akka.actor.{Props, Actor}
import akka.event.Logging
import models.customer.ContactIn

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsArray}
import akka.pattern.ask

import services.{CrudServiceInternal, OrderCreateMailRequest, OrderPrintServiceInternal}
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
                                 customerContactId: String,
                                 orderNumber: String,
                                 message: MessageTexts)

case class MessageTexts(text: String,
                        html: String)

class OrderMailer(crudService: CrudServiceInternal, printer: OrderPrintServiceInternal) extends Actor with S3Bucket {

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
                storedLink <- printer.storeInternal(req.storageKey, new ByteArrayInputStream(reportContainer.bytes))

              } yield {
                log.debug(s"order report for mail stored at: $storedLink")

                val mailParams = OrderMailBody(customerName, req.url(bucketName), orderNumber)



                val params = OrderMailIngredients(contactId, orderNumber, createTexts(mailParams))

                sendMail(params)
              }

              res match {
                case None => log.error(s"cannot gather mail contents for order: ${req.orderId}")
                case _ => log.debug(s"order report mail sent for order: ${req.orderId}")
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


  private def sendMail(params: OrderMailIngredients) = {
    implicit val timeout = Timeout(5 seconds)
    crudService.getById[ContactIn](params.customerContactId).map {
      _.map {
        _.map {
          (contact) => {
            firstMailAddress(contact).map {
              (customerMail) => {

                val mail: Mail = Mail(Seq("tsechov@gmail.com", "zsoldoszsolt@gmail.com"), subject = s"SzamosKölyök rendelés [${params.orderNumber}]", message = params.message.text, richMessage = Some(params.message.html))
                val result: Future[Try[String]] = (mailer ? mail).mapTo[Try[String]]
                result.andThen {
                  case Failure(t) => mailError(t, mail)
                  case Success(res: Failure[String]) => mailError(res.exception, mail)
                  case Success(res: Success[String]) => log.debug(s"mail sent with id: ${res.get}\n$mail\n")
                }
              }
            }
          }
        }
      }
    }

  }

  private def mailError(t: Throwable, mail: Mail) = {
    log.error(t, s"couldnt send email: \n$mail\n")
  }

  private def firstMailAddress(from: JsObject): Option[String] = {
    (from \ "emails").as[JsArray].value.map(v => (v \ "address").asOpt[String]).headOption.flatten.flatMap {
      case s: String if s.trim.isEmpty => None
      case s => Some(s)
    }
  }
}
