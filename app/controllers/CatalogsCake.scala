package controllers

import play.api.mvc.{Action, Controller}
import models.{Catalog, AssetSupport}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import services.catalogs.production

object CatalogsCake extends Controller {


  val catalogsService = production service

  //import models.Catalog.collectionName

  def getById(id: AssetSupport.IdType) = Action.async {

      catalogsService.getById[Catalog](id).map {
        case Some(catalog) => Ok(Json.toJson(catalog))
        case None => NotFound
      }

  }
}
