package services.mailer

import java.io.ByteArrayInputStream

import akka.actor.{Props, Actor}
import akka.event.Logging
import models.customer.ContactIn

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsArray}
import akka.pattern.ask

import services.{CrudServiceInternal, OrderMailRequest, OrderPrintServiceInternal}
import services.mailer.order.support.Mail
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Success, Failure, Try}
import scala.concurrent.Future


object OrderMailer {
  def props(crudService: CrudServiceInternal, printer: OrderPrintServiceInternal): Props = Props(new OrderMailer(crudService, printer))
}

case class OrderMailIngredients(customerName: String, customerContactId: String, agentEmail: String, orderLink: String)

class OrderMailer(crudService: CrudServiceInternal, printer: OrderPrintServiceInternal) extends Actor {

  val log = Logging(context.system, this)
  val mailer = context.actorOf(Props[MailSender])

  override def receive = {
    case req: OrderMailRequest => {
      log.debug(s"received OrderMailRequest: $req")

      val res = printer.getPdf(req.orderId).map {
        _.map {
          _.map {
            (reportContainer) =>

              val customerName = (reportContainer.customer \ "name").asOpt[String]
              val contactId = (reportContainer.customer \ "contactIds").as[JsArray].value.headOption.flatMap(_.asOpt[String])
              val agentEmail = firstMailAddress(reportContainer.agent)
              val link = printer.storeInternal(req.storageKey, new ByteArrayInputStream(reportContainer.bytes))

              sendMail(OrderMailIngredients(customerName.get, contactId.get, agentEmail.get, link.get))
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
                val mail: Mail = Mail(Seq(customerMail, params.agentEmail), subject = "SzamosKolyok rendeles", message = "text", richMessage = Some("html"))
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
