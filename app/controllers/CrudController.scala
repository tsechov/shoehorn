package controllers

import services.catalogs.production
import models._
import play.api.libs.json._
import play.api.mvc._
import play.api.Logger
import scala.util.{Failure, Success, Try}
import services.CollectionName
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime
import play.api.http.{HeaderNames, ContentTypes}
import scala.util.Failure
import scala.Some
import scala.util.Success
import play.api.libs.json.JsObject
import play.api.libs.json.Json._
import scala.util.Failure
import scala.Some
import scala.util.Success
import play.api.libs.json.JsObject
import scala.util.Failure
import scala.Some
import play.api.mvc.SimpleResult
import scala.util.Success
import play.api.libs.json.JsObject
import scala.util.Failure
import scala.Some
import play.api.mvc.SimpleResult
import play.api.mvc.Call
import scala.util.Success
import play.api.libs.json.JsObject

trait CrudController extends Results with ControllerUtils {
  private val service = production service

  def getById[A](id: AssetSupport.IdType)(implicit f: Format[A], ev: CollectionName[A]): Future[SimpleResult] = {
    service.getById[A](id).map {
      case Some(entity) => Ok(Json.toJson(entity))
      case None => NotFound
    }
  }

  def find[A](q: Option[String])(implicit f: Format[A], ev: CollectionName[A]): Future[SimpleResult] = {
    Logger.debug(s"find queryString: $q")

    q match {
      case Some(queryString) => {
        Try(Json.parse(queryString).as[JsObject]) match {
          case Success(queryJson) => service.find[A](queryJson).map(foundOrNot[A])
          case Failure(error) => badQuery(queryString, error)
        }
      }
      case None => service.findAll[A].map(foundOrNot[A])
    }
  }

  def create[C <: AssetCreate[A], A](input: JsValue, getByIdRoute: String => Call)(implicit r: Reads[C], w: Writes[A], ev: CollectionName[A]) = {

    input.validate[C] match {
      case JsSuccess(validated, _) => {
        val futureResult = service.insert[C, A](validated)
        futureResult.map {
          internalServerError[AssetSupport.IdType]("error inserting") orElse {
            case Success(id) => {
              Logger.debug(s"Successfully inserted with id: $id")
              Created.as(ContentTypes.JSON)
                .withHeaders(HeaderNames.LOCATION -> locationUrl(id, getByIdRoute))
            }
          }
        }
      }
      case error: JsError => {
        val jsonError = JsError.toFlatJson(error)
        Logger.debug("invalid input json for create: " + prettyPrint(jsonError))
        Future.successful(BadRequest(jsonError))
      }
    }


  }

  def update[A <: AssetUpdate[U], U](id: AssetSupport.IdType, input: JsValue)(implicit r: Reads[A], ev: CollectionName[A]) = {
    input.validate[A] match {
    badJsonRequest("") orElse {}
      case JsSuccess(entity, _) => {
        val futureResult = service.update[A, U](entity)
        futureResult.map {
          internalServerError("error updating") orElse {
            case Success(_) => {
              Logger.debug(s"Successfully updated with id: $id")
              Ok
            }

          }
        }
      }
      case error: JsError => {
        val jsonError = JsError.toFlatJson(error)
        Logger.debug("invalid input json for update: " + prettyPrint(jsonError))
        Future.successful(BadRequest(jsonError))
      }
    }


  }
}
