package controllers

import play.api.mvc.{AnyContent, Action}
import models.{Catalog, AssetSupport}


object CatalogsCake extends CrudController {

  def getById(id: AssetSupport.IdType): Action[AnyContent] = Action.async {
    getById[Catalog](id)

  }

  def find(q: Option[String]): Action[AnyContent] = Action.async {
    find[Catalog](q)
  }

}
