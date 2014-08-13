package controllers

import play.api.mvc.{Call, Action}
import models.AssetSupport.IdType
import models.Catalog
import models.CatalogCreate
import models.CatalogUpdate
import play.api.mvc.BodyParsers.parse
import play.api.libs.json.JsValue


object Catalogs extends CrudController {
  type MODEL = Catalog
  type UPDATEMODEL = CatalogUpdate
  type CREATEMODEL = CatalogCreate


  def create = Action.async(parse.json) {
    request =>
      super.create[CREATEMODEL, MODEL](request.body, id => controllers.routes.Catalogs.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update[MODEL, UPDATEMODEL](id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete[MODEL](id)
  }

}
