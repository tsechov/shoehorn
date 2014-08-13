package controllers

import services.{production, CollectionName}

import models._
import play.api.libs.json._
import play.api.mvc._
import play.api.{Play, Logger}
import scala.util.{Failure, Success, Try}
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
import play.api.mvc.BodyParsers.parse

trait CrudController extends Results with ControllerUtils {

  type MODEL
  type UPDATEMODEL
  type CREATEMODEL <: AssetCreate[MODEL]


  private val service = production service

  def getById(id: AssetSupport.IdType)(implicit f: Format[MODEL], ev: CollectionName[MODEL]) = Action.async {
    service.getById[MODEL](id).map {
      internalServerError[Option[MODEL]]("[getById] error") orElse {
        case Success(result) => result match {
          case Some(entity) => Ok(Json.toJson(entity))
          case None => NotFound
        }
      }
    }
  }

  def find(q: Option[String])(implicit f: Format[MODEL], ev: CollectionName[MODEL]) = Action.async {
    Logger.debug(s"find queryString: $q")

    q match {
      case Some(queryString) => {
        Try(Json.parse(queryString).as[JsObject]) match {
          case Success(queryJson) => service.find[MODEL](queryJson).map(listResult[MODEL]("find"))
          case Failure(error) => badQuery(queryString, error)
        }
      }
      case None => service.findAll[MODEL].map(listResult[MODEL]("find"))
    }
  }


  def create[CREATEMODEL <: AssetCreate[MODEL], MODEL](input: JsValue, getByIdRoute: String => Call)(implicit r: Reads[CREATEMODEL], w: Writes[MODEL], ev: CollectionName[MODEL]) = {

    Logger.debug("input for create: \n" + Json.prettyPrint(input))
    def operation[CREATEMODEL <: AssetCreate[MODEL], MODEL](implicit w: Writes[MODEL], ev: CollectionName[MODEL]) = service.insert[CREATEMODEL, MODEL] _
    def createdResult(id: AssetSupport.IdType, msg: String): SimpleResult = {
      Logger.debug(s"$msg with id: $id")
      Created.as(ContentTypes.JSON)
        .withHeaders(HeaderNames.LOCATION -> locationUrl(id, getByIdRoute))
    }

    (performOperation[CREATEMODEL, AssetSupport.IdType]("create", operation[CREATEMODEL, MODEL], createdResult))(input.validate[CREATEMODEL])

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
