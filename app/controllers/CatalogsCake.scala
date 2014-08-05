package controllers

import play.api.mvc.{AnyContent, Action}
import models._
import play.api.mvc.BodyParsers.parse
import play.api.libs.json.JsValue


object CatalogsCake extends CrudController {

  def getById(id: AssetSupport.IdType): Action[AnyContent] = Action.async {
    getById[Catalog](id)

  }

  def find(q: Option[String]): Action[AnyContent] = Action.async {
    find[Catalog](q)
  }

  def create: Action[JsValue] = Action.async(parse.json) {
    request => super.create[CatalogCreate, Catalog](request.body, id => controllers.routes.CatalogsCake.getById(id))
  }

  def update(id: AssetSupport.IdType) = Action.async(parse.json) {
    request =>
      super.update[Catalog, CatalogUpdate](id, request.body)
  }

}
