package controllers

import play.api.mvc.{SimpleResult, Action, Controller}
import models.{Catalog, AssetSupport}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsArray, Json}
import services.catalogs.production
import play.api.Logger
import scala.util.{Failure, Success, Try}
import play.api.http.ContentTypes

object CatalogsCake extends Controller with ControllerUtils{


  val service = production service

  //import models.Catalog.collectionName

  def getById(id: AssetSupport.IdType) = Action.async {

    service.getById[Catalog](id).map {
        case Some(catalog) => Ok(Json.toJson(catalog))
        case None => NotFound
      }

  }

  def f: PartialFunction[Throwable, Future[SimpleResult]] = {
    case t:Throwable => Future.successful(Ok)
  }

  def find(q: Option[String]) = Action.async {
    request =>

      Logger.debug(s"find queryString: $q")

      q match {
        case Some(queryString) => {
           Try(Json.parse(queryString)) match {
            case Success(queryJson) => service.find[Catalog](queryJson).map(foundOrNot[Catalog])
            case Failure(error) => badQuery(queryString,error)
          }
        }
        case None => service.findAll[Catalog].map(foundOrNot[Catalog])
        }
      }





}
