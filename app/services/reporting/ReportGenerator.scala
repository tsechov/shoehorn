package services.reporting

import java.util.UUID

import akka.actor.{Props, Actor}
import akka.event.Logging

import services.{OrderPrintServiceInternal, OrderReportRequest}



object ReportGenerator{
  def props(printer: OrderPrintServiceInternal): Props = Props(new ReportGenerator(printer))
}


class ReportGenerator(orderPrintService:OrderPrintServiceInternal) extends Actor {

  val log = Logging(context.system, this)

  override def receive = {
    case req: OrderReportRequest => {
      log.debug(s"received go for $req")
      orderPrintService.storePdf(req)

    }
    case _ => log.error("unknown message received")
  }
}
