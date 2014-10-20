package services.reporting

import akka.actor.{Props, ActorSystem, Actor}
import models.AssetSupport.IdType
import java.util.UUID
import org.joda.time.DateTime
import services.{OrderPrintServiceInternal, OrderService}
import services.storage.{S3Service, StorageService}
import scala.collection.mutable

sealed trait OrderReportMessage


case class OrderReportRequest(id: UUID, orderId: IdType) extends OrderReportMessage

case class OrderReportQuery(id: UUID) extends OrderReportMessage

object ReportController {

  def props(req: OrderReportRequest) = Props(new ReportGenerator(req))
}

class ReportController(printer: OrderPrintServiceInternal) extends Actor {


  override def receive = {
    case req: OrderReportRequest => {
        val gen = context.actorOf(ReportController.props(req), s"ReportGenerator-${req.id}")
        gen ! Go
      }
    }




}
