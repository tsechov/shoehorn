package controllers

import akka.actor.Props
import controllers.utils.{S3Bucket, CrudController}
import models.AssetSupport.IdType
import play.api.libs.concurrent.Akka
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.reporting.ReportGenerator
import scala.concurrent.Future
import scalax.file.FileSystem
import services.{OrderReportRequest, runtime}
import scala.util.{Failure, Success}
import play.api.Logger
import java.util.UUID
import play.api.Play.current
import services.ConfigSupport.configKey


object Reports extends CrudController with S3Bucket{
  lazy val orderPrintService = runtime orderPrintService

  lazy val reports = Akka.system.actorOf(ReportGenerator.props(orderPrintService), name = "reportgenerator")

  val reportsFolder=configKey("aws.s3.reports.folder","reports")




  def order(id: IdType) = Action.async {

    orderPrintService.getPdf(id).map {
      _ match {
        case Success(op) => {
          op match {
            case Some(report) => {
              val targetFile = FileSystem.default.createTempFile(suffix = ".shoehorn-order.pdf")
              println(s"path: ${targetFile.toAbsolute.path}")
              targetFile.outputStream().write(report.bytes)
              Ok.sendFile(targetFile.jfile, true, onClose = () => {
                targetFile.delete()
              }).as("application/pdf")
            }
            case None => NotFound(s"some data is missing for the order with the given id: ${id}")
          }
        }
        case Failure(e) => {
          val logId = UUID.randomUUID().toString
          Logger.error(s"[$logId] - cannot generate report", e)
          InternalServerError(s"cannot generate report! logId: [${logId}]")
        }
      }

    }

  }

  def genOrder(orderId:IdType) = Action.async {


    val uid=UUID.randomUUID()
    val req=OrderReportRequest(uid,orderId,reportsFolder)
    reports ! req
    Future.successful(Ok(Json.obj("reportUrl"->req.url(bucketName))))
  }


}
