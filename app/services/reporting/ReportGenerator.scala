package services.reporting

import akka.actor.Actor
import akka.event.Logging

object Go

class ReportGenerator(request: OrderReportRequest) extends Actor {
  val log = Logging(context.system, this)

  override def receive = {
    case g: Go => {
      log.debug(s"received go for $request")
    }
    case _ => log.error("unknown message received")
  }
}
