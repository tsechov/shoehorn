package controllers

import play.api.mvc.{Action, Controller}
import services.real
import models.AssetSupport
import scala.concurrent.Future

object CatalogsCake extends Controller {

  val catalogsService = real catalogService

  def getById(id: AssetSupport.IdType) = Action.async {
    request =>
      val result = catalogsService.getById(id)
      Future.successful(Ok)
  }
}
