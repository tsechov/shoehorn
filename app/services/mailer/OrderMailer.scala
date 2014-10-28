package services.mailer

import akka.actor.{Props, Actor}
import akka.event.Logging
import models.order.OrderIn
import play.api.libs.json.Json

import services.{DbQuery, CrudServiceInternal, OrderMailRequest, OrderPrintServiceInternal}


object OrderMailer{
  def props(crudService:CrudServiceInternal, printer: OrderPrintServiceInternal): Props = Props(new OrderMailer(crudService,printer))
}
class OrderMailer(crudService:CrudServiceInternal,printer: OrderPrintServiceInternal) extends Actor{
  val log = Logging(context.system, this)
  override def receive={
    case req:OrderMailRequest => {
      log.debug(s"received OrderMailRequest: $req")
      printer.storePdf(req)
      crudService.find[OrderIn](DbQuery(Json.obj("_id"->req.orderId),Some(Json.obj("originatorId"->1,"agentId"->1))))
    }
  }
}
