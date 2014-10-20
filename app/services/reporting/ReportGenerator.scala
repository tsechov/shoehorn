package services.reporting

import java.util.UUID

import akka.actor.Actor
import akka.event.Logging


sealed trait GoMessage
case object Go extends GoMessage
abstract sealed class OrderReportResult{
  def id:UUID
}

case class OrderReportSuccess(id:UUID,fileName:String) extends OrderReportResult
case class OrderReportFailure(id:UUID,cause:Throwable) extends OrderReportResult



class ReportGenerator(request: OrderReportRequest) extends Actor {

  val log = Logging(context.system, this)
  log.debug(s"created actor for $request")

  override def receive = {
    case g: GoMessage => {
      log.debug(s"received go for $request")

    }
    case _ => log.error("unknown message received")
  }
}
