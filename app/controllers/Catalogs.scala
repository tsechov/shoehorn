package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType
import play.api.mvc.BodyParsers.parse
import models.catalog.{Catalog, CatalogUpdate, CatalogCreate}


object Catalogs extends CrudController {
  override type MODEL = Catalog
  override type UPDATEMODEL = CatalogUpdate
  override type CREATEMODEL = CatalogCreate


  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Catalogs.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}
