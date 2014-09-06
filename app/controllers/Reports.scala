package controllers

import controllers.utils.CrudController
import models.AssetSupport.IdType
import play.api.mvc.Action
import java.io.File
import scala.concurrent.Future
import org.apache.poi.util.IOUtils
import scalax.io.Resource


object Reports extends CrudController {

  def order(id: IdType) = Action.async {
    Future.successful {
      val resource = getClass.getResource("/test.pdf");
      val pdfBytes = IOUtils.toByteArray(resource.openStream())

      val targetFile = File.createTempFile("foo", " bar")
      Resource.fromFile(targetFile).write(pdfBytes)


      Ok.sendFile(targetFile, true)
        .as("application/pdf")

    }
  }
}
