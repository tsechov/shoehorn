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

import services._
import services.mailer.order.support.{OrderMailBody, Mail}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Success, Failure, Try}
import scala.concurrent.Future
import services.storage.{StreamAndLength, S3Bucket}
import services.mailer.order.support._
import play.api.http.MimeTypes
import org.joda.time.DateTime
import models.{DateFormatSupport, AssetIn, AssetInCompanion}
import services.OrderCreateMailRequest
import play.api.libs.json.JsArray
import scala.util.Failure
import scala.Some
import services.mailer.order.support.Mail
import scala.util.Success
import services.mailer.order.support.OrderMailBody
import play.api.libs.json.JsObject

import play.api.Play.current
import reactivemongo.bson.BSONObjectID

object OrderMailer {
  def props(mongo: MongoDb, crudService: CrudServiceInternal, printer: OrderPrintServiceInternal): Props = Props(new OrderMailer(mongo, crudService, printer))
}

case class OrderMailIngredients(
                                 agentEmail: String,
                                 customerContactId: String,
                                 orderNumber: String,
                                 message: MessageTexts,
                                 req: MailRequest,
                                 reportGenerationTime: Long)

case class MessageTexts(text: String,
                        html: String)

case class MailResult(_id: IdType = BSONObjectID.generate.stringify,
                      createdAt: DateTime = new DateTime(),
                      orderId: IdType,
                      to: List[String],
                      reportGenerationTime: Long,
                      success: Boolean = false,
                      resultsKey: String,
                      reportDataLength: Option[Long] = None,
                      reportStorageKey: Option[String] = None,
                      mailId: Option[String] = None,
                      error: Option[String] = None
                       )

object MailResult extends DateFormatSupport {

  implicit val format = Json.format[MailResult]

  implicit def cn: CollectionName[MailResult] = new CollectionName[MailResult] {
    override def get: String = "mails"
  }
}


class OrderMailer(mongo: MongoDb, crudService: CrudServiceInternal, printer: OrderPrintServiceInternal) extends Actor with S3Bucket {
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
                agent = reportContainer.agent
                agentEmail <- firstMailAddress(agent).orElse(mailAddressNotFound(agent))
                storedLink <- printer.storePdfInternal(req.storageKey, StreamAndLength(new ByteArrayInputStream(reportContainer.bytes), reportContainer.bytes.length))

              } yield {
                log.debug(s"order report for mail stored at: $storedLink")

                val mailParams = OrderMailBody(s"$customerName ${reportContainer.companyType}", req.url(bucketName), orderNumber)
                //FIXME: reportgenerationtime
                val params = OrderMailIngredients(agentEmail, contactId, orderNumber, createTexts(mailParams), req, 0L)

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
                  case "prod" => List(customerMail, params.agentEmail)
                  case "dev" => List("tsechov@gmail.com", "zsoldoszsolt@gmail.com")
                }
                val mail = Mail(recipients, subject = s"SzamosKölyök rendelés [${params.orderNumber}]", message = params.message.text, richMessage = Some(params.message.html))

                val result = (mailer ? mail).mapTo[Try[String]]


                result.onFailure { case t => mailError(t, params, mail)}
                result.onSuccess {
                  case res: Failure[String] => mailError(res.exception, params, mail)
                  case res: Success[String] => {
                    log.debug(s"mail sent with id: ${res.get} for order: ${params.req.orderId}")
                    val result = MailResult(orderId = params.req.orderId, to = recipients, resultsKey = params.req.resultKey, reportStorageKey = Some(params.req.storageKey), reportGenerationTime = params.reportGenerationTime, success = true, mailId = Some(res.get))
                    persistAndStoreMailResult(result)
                    log.debug(s"mail result stored at: ${params.req.resultUrl(bucketName)} for order: ${params.req.orderId}")
                  }
                }
              }
            }.orElse(mailAddressNotFound(contact))
          }
        }
      }
    }

  }

  private def mailAddressNotFound(obj: JsObject) = {

    log.error(s"no email address found in contact: ${(obj \ "_id").asOpt[String]}")
    None

  }


  private def createTexts(mailParams: OrderMailBody): MessageTexts = {
    val textMessage = orderCreateMailAsText(mailParams)
    val htmlMessage = orderCreateMailAsHtml(mailParams)
    MessageTexts(textMessage, htmlMessage)
  }

  private def persistAndStoreMailResult(result: MailResult) = {
    val resultJson = Json.toJson(result)
    mongo.insert[MailResult](resultJson).onFailure {
      case t => log.error(t, s"couldnt presist mail result: $result")
    }
    val datas = Json.prettyPrint(resultJson).getBytes
    val content = StreamAndLength(new ByteArrayInputStream(datas), datas.length, MimeTypes.JSON)
    printer.storeInternal(result.resultsKey, content)
  }

  private def mailError(t: Throwable, params: OrderMailIngredients, mail: Mail) = {
    val result = MailResult(orderId = params.req.orderId, to = mail.to, resultsKey = params.req.resultKey, reportGenerationTime = params.reportGenerationTime, error = Some(Option(t.getMessage).getOrElse("unknown error")))
    persistAndStoreMailResult(result)

    log.error(t, "couldnt send email")
  }

  private def firstMailAddress(from: JsObject): Option[String] = {
    (from \ "emails").as[JsArray].value.map(v => (v \ "address").asOpt[String]).headOption.flatten.flatMap {
      case s: String if s.trim.isEmpty => None
      case s => Some(s)
    }
  }
}
