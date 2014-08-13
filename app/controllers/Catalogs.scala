package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType
import models.Catalog
import models.CatalogCreate
import models.CatalogUpdate
import play.api.mvc.BodyParsers.parse
import play.api.libs.json.JsValue


object Catalogs extends CrudController {

  def getById(id: IdType) = Action.async {
    super.getById[Catalog](id)
  }

  def find(q: Option[String]) = Action.async {
    super.find[Catalog](q)
  }

  def create = Action.async(parse.json) {
    request =>
      super.create[CatalogCreate, Catalog](request.body, id => controllers.routes.Catalogs.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update[Catalog, CatalogUpdate](id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete[Catalog](id)
  }

}
