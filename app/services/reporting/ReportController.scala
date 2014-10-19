package services.reporting

import akka.actor.{Props, ActorSystem, Actor}
import models.AssetSupport.IdType
import java.util.UUID
import org.joda.time.DateTime
import scala.collection.mutable

sealed trait OrderReportMessage


case class OrderReportRequest(id: UUID, orderId: IdType, start: DateTime) extends OrderReportMessage

case class OrderReportQuery(id: UUID) extends OrderReportMessage

object ReportController {

  def props(req: OrderReportRequest) = Props(new ReportGenerator(req) )
}

class ReportController extends Actor {
  private val requests: mutable.ListBuffer[OrderReportRequest] = mutable.ListBuffer()

  override def receive = {
    case req: OrderReportRequest => {
      if (!requests.contains(req)) {
        requests.+=(req)
        val gen = context.actorOf(ReportController.props(req), s"ReportGenerator-${req.id}")
        gen ! Go
      }
    }
    case query: OrderReportQuery => {
      sender ! requests.find(_.id == query.id).isDefined
    }


  }
}
