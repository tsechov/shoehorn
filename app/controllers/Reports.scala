package controllers

import controllers.utils.CrudController
import models.AssetSupport.IdType
import play.api.mvc.Action
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalax.file.FileSystem
import services.production
import scala.util.{Failure, Success}
import play.api.Logger
import java.util.UUID


object Reports extends CrudController {
  lazy val orderPrintService = production orderPrintService

  def order(id: IdType) = Action.async {

    orderPrintService.getPdf(id).map {
      _ match {
        case Success(op) => {
          op match {
            case Some(pdfBytes) => {
              val targetFile = FileSystem.default.createTempFile(suffix = ".shoehorn-order.pdf")
              println(s"path: ${targetFile.toAbsolute.path}")
              targetFile.outputStream().write(pdfBytes)
              Ok.sendFile(targetFile.jfile, true, onClose = () => {
                targetFile.delete()
              }).as("application/pdf")
            }
            case None => NotFound(s"some data is missing for the order with the given id: ${id}")
          }
        }
        case Failure(e) => {
          val logId = UUID.randomUUID().toString
          Logger.error(s"${logId} cannot generate report", e)
          InternalServerError(s"cannot generate report! logId: [${logId}]")
        }
      }

    }

  }
}
