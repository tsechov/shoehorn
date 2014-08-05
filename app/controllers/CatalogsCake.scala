package controllers

import play.api.mvc.{AnyContent, Action}
import models._
import play.api.mvc.BodyParsers.parse
import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime
import scala.util.{Failure, Success}
import play.api.Logger
import play.api.http.{HeaderNames, ContentTypes}
import play.api.libs.json.{JsValue, JsError}
import play.api.libs.json.Json._
import scala.util.Success
import scala.util.Failure
import scala.concurrent.Future


object CatalogsCake extends CrudController {

  def getById(id: AssetSupport.IdType): Action[AnyContent] = Action.async {
    getById[Catalog](id)

  }

  def find(q: Option[String]): Action[AnyContent] = Action.async {
    find[Catalog](q)
  }

  def create: Action[JsValue] = Action.async(parse.json) {
    request => super.create[CatalogCreate,Catalog](request.body,id => controllers.routes.CatalogsCake.getById(id))
  }

  def update(id: AssetSupport.IdType) = Action.async(parse.json) {
    request =>
      val json = request.body
      super.update[Catalog,CatalogUpdate](id,json)


  }

}
