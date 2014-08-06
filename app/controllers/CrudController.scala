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
    Logger.debug("input for create: \n" + Json.prettyPrint(input))
    def operation[C <: AssetCreate[A], A](implicit w: Writes[A], ev: CollectionName[A]) = service.insert[C, A] _
    def createdResult(id: AssetSupport.IdType, msg: String): SimpleResult = {
      Logger.debug(s"$msg with id: $id")
      Created.as(ContentTypes.JSON)
        .withHeaders(HeaderNames.LOCATION -> locationUrl(id, getByIdRoute))
    }


    (performOperation[C, AssetSupport.IdType]("create", operation[C, A], createdResult))(input.validate[C])

  }

  def update[A <: AssetUpdate[U], U](id: AssetSupport.IdType, input: JsValue)(implicit r: Reads[A], w: Writes[U], ev: CollectionName[U]) = {
    Logger.debug("input for update: \n" + Json.prettyPrint(input))
    def operation[A <: AssetUpdate[U], U](implicit w: Writes[U], ev: CollectionName[U]) = service.update[A, U](id) _
    def okResult(n: Unit, msg: String): SimpleResult = {
      Logger.debug(s"$msg with id: $id")
      Ok
    }
    (performOperation[A, Unit]("update", operation[A, U], okResult))(input.validate[A])

  }

  def delete[A: CollectionName](id: AssetSupport.IdType) = {
    val result = service.remove[A](id)
    result.map {
      internalServerError[Unit](s"[delete] error with id: $id") orElse {
        case Success(_) => {
          Logger.debug(s"[delete] successful with id: $id")
          Ok
        }
      }
    }
  }
}
