package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType
import play.api.mvc.BodyParsers.parse
import models.product.{ProductIn, ProductUpdate, ProductCreate}
import controllers.utils.CrudController
import play.api.libs.json.{JsArray, Json, JsObject}
import play.api.Logger
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.{Failure, Success, Try}
import services.DbQuery


object Products extends CrudController {
  override type MODEL = ProductIn
  override type UPDATEMODEL = ProductUpdate
  override type CREATEMODEL = ProductCreate

  def find(q: Option[String], catalogs: Option[String]) = Action.async {
    Logger.debug(s"q: $q; catalogs: $catalogs")
    if (q.isDefined && catalogs.isDefined)
      Future.successful(BadRequest("q and catalogs are mutually exclusive in query"))
    else if (q.isEmpty && catalogs.isEmpty)
      service.findAll[MODEL].map(listResult[JsObject]("find"))
    else if (q.isDefined)
      super.findByQuery(q.get)
    else {
      Try(Json.parse(catalogs.get).as[JsArray]) match {
        case Success(catalogIds) => {
          val ids = catalogIds.as[List[String]]

          if (ids.isEmpty)
            service.findAll[MODEL].map(listResult[JsObject]("find"))
          else {
            val exprs = ids.foldLeft(JsArray())((array, id) => array :+ Json.obj("catalogs.catalogId" -> id))
            val query = DbQuery(Json.obj("$or" -> exprs))
            Logger.debug(s"query: $query")

            service.find[MODEL](query).map(listResult[JsObject]("find by catalogIds"))
          }


        }
        case Failure(error) => badQuery(catalogs.get, error)
      }
    }

  }

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Products.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}
