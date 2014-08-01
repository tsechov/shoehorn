package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.{Cursor}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json._
import reactivemongo.bson.BSONObjectID

import play.api.http.{HeaderNames, ContentTypes}
import org.joda.time.DateTime

import play.api.{Logger, Play}
import models._
import scala.util.{Failure, Success, Try}
import OptionWrapperImplicits._
import LastErrorWrapperImplicits._
import play.api.libs.json.JsArray
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.util.Failure
import scala.Some
import scala.util.Success
import play.api.libs.json.JsObject

//TODO: generalize
@Singleton
class Catalogs extends Controller with MongoController with CatalogPaths with ControllerUtils with MongoUtils{

  def collection: JSONCollection = db.collection[JSONCollection]("catalogs")



  private def locationUrl(id: String) = contextUrl + controllers.routes.Catalogs.getById(id).toString



  def create = Action.async(parse.json) {
    request =>
      val json = request.body
      val id = BSONObjectID.generate.stringify
      val now = new DateTime()
      val transformer = AssetTransform.create(json)(id, now)

      val result = for {
        validated <- json.validate[CatalogCreate]
        transformed <- json.transform(transformer)
      } yield collectionInsert(transformed)

      result.map {
        futureResult => futureResult.map {
          tried => tried match {
            case Success(lastError) => {
              Logger.debug(s"Successfully inserted with id: $id")
              Created.as(ContentTypes.JSON)
                .withHeaders(HeaderNames.LOCATION -> locationUrl(id))
            }
            case Failure(error) => {
              Logger.debug(s"error inserting: $error")
              InternalServerError
            }
          }
        }

      }.recoverTotal(error => {
        val jsonError = JsError.toFlatJson(error)
        Logger.debug("invalid input json for create: " + prettyPrint(jsonError))
        Future.successful(BadRequest(jsonError))
      })
  }

  def update(id: AssetSupport.IdType) = Action.async(parse.json) {
    request =>
      val json = request.body
      val now = new DateTime()
      val transformer = AssetTransform.update(json)(now)
      val result = for {
        transformed <- {
          println("transform :" + json);
          json.transform(transformer)
        }
        validated <- {
          println("validate: " + transformed);
          transformed.validate[CatalogUpdate]
        }

      } yield (validated, transformed)

      result.map {
        entity => {
          val selector = obj(AssetSupport.idFieldName -> id)

          val modifier = obj("$set" -> entity._2)
          collection.update(selector, modifier).map {
            lastError =>
              Logger.debug(s"Successfully updated with id: $id")
              Ok
          }
        }
      }.recoverTotal(error => {
        Logger.debug(s"invalid input json for update: $id \n" + JsError.toFlatJson(error))
        Future.successful(BadRequest(JsError.toFlatJson(error)))
      })


  }

  def getById(id: AssetSupport.IdType) = Action.async {
    val query = obj(AssetSupport.idFieldName -> id)

    val cursor = collection.find(query).cursor[Catalog].collect[List]()

    val futureJson = cursor.map {
      case head :: _ => Some(toJson(head))
      case Nil => None
    }

    futureJson.map {
      case Some(jsObject) => Ok(jsObject)
      case None => NotFound
    }

  }

  def find(q: Option[String]) = Action.async {
    request =>

      Logger.debug(s"find queryString: $q")

      q match {
        case Some(queryString) => {
          Try(Json.parse(queryString)) match {
            case Success(queryJson) => {
              collectionFind[Catalog](Some(queryJson)).map {
                jsArray => if (jsArray.value.isEmpty) NotFound else Ok(jsArray).as(ContentTypes.JSON)
              }
            }
            case Failure(error) => {
              Logger.debug(s"error parsing query: $queryString")
              Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
            }
          }
        }
        case None => collectionFind[Catalog]().map {
          Ok(_).as(ContentTypes.JSON)
        }
      }

  }







  def delete(id: String) = Action.async {
    val query = Json.obj(AssetSupport.idFieldName -> id)
    val result = collection.remove(query)
    result.map {
      lastError => {
        Logger.debug(s"Successfully deleted with id: $id")
        Ok
      }
    }

  }
}
