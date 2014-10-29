package services.mailer

import java.io.ByteArrayInputStream

import akka.actor.{Props, Actor}
import akka.event.Logging
import models.customer.ContactIn

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsArray}
import services.storage.StorageComponentInternal

import services.{CrudServiceInternal, OrderMailRequest, OrderPrintServiceInternal}


object OrderMailer{
  def props(crudService:CrudServiceInternal, printer: OrderPrintServiceInternal): Props = Props(new OrderMailer(crudService,printer))
}
class OrderMailer(crudService:CrudServiceInternal,printer: OrderPrintServiceInternal) extends Actor{

  val log = Logging(context.system, this)

  override def receive={
    case req:OrderMailRequest => {
      log.debug(s"received OrderMailRequest: $req")

      val res=printer.getPdf(req.orderId).map {
        _.map {
          _.map{
            (reportContainer) =>
              val link=printer.storeInternal(req.storageKey,new ByteArrayInputStream(reportContainer.bytes))
              val agentEmail = (reportContainer.agent \ "emails").as[JsArray].value.map(v => (v \ "address").asOpt[String]).headOption.flatten

          }
        }
      }


    }
  }

  private def customerMail(customer:JsObject):Option[String]={
    val contactMail=(customer \ "contactIds").as[JsArray].value.headOption.map {
      (contactIdValue) => {
        val ccc=contactIdValue.asOpt[String].map{
          (contactId) => {
            val contact=crudService.getById[ContactIn](contactId)
            contact.map {
              _.map {
                _.map {
                  (contact) => {
                    ???
                  }
                }
              }
            }
          }
        }



      }

    }


    ???
  }
}
