package controllers

import services.catalogs.production
import models.{Catalog, AssetSupport}
import play.api.libs.json.{JsObject, Format, Reads, Json}
import play.api.mvc.{SimpleResult, Results}
import play.api.Logger
import scala.util.{Failure, Success, Try}
import services.CollectionName
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

trait CrudController extends Results with ControllerUtils {
  private val service = production service

  def getById[A](id: AssetSupport.IdType)(implicit f: Format[A],ev:CollectionName[A]):Future[SimpleResult] ={
    service.getById[A](id).map {
      case Some(entity) => Ok(Json.toJson(entity))
      case None => NotFound
    }
  }

  def find[A](q: Option[String])(implicit f: Format[A],ev:CollectionName[A]):Future[SimpleResult]={
    Logger.debug(s"find queryString: $q")

    q match {
      case Some(queryString) => {
        Try(Json.parse(queryString).as[JsObject]) match {
          case Success(queryJson) => service.find[A](queryJson).map(foundOrNot[A])
          case Failure(error) => badQuery(queryString,error)
        }
      }
      case None => service.findAll[A].map(foundOrNot[A])
    }
  }

}
