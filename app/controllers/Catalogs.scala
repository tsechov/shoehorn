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
import models.{CatalogPaths, CatalogUpdate, CatalogSupport, CatalogCreate}
import scala.util.{Failure, Success, Try}
import OptionWrapperImplicits._
import LastErrorWrapperImplicits._


@Singleton
class Catalogs extends Controller with MongoController with CatalogPaths {

  def collection: JSONCollection = db.collection[JSONCollection]("catalogs")

  private val contextUrl = Play.application(play.api.Play.current).configuration.getString("context.url").getOrElse("")

  private def locationUrl(id: String) = contextUrl + controllers.routes.Catalogs.getById(id).toString

  import models.Catalog

  def create = Action.async(parse.json) {
    request =>
      val json = request.body
      val id = BSONObjectID.generate.stringify
      val now = new DateTime()
      val transformer = CatalogCreate.transformer(json)(id, now)

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

  def update(id: Catalog.IdType) = Action.async(parse.json) {
    request =>
      val json = request.body
      val now = new DateTime()
      val transformer = CatalogUpdate.transformer(json)(now)
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
          val selector = obj(CatalogSupport.idFieldName -> id)

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

  def getById(id: Catalog.IdType) = Action.async {
    val query = obj(CatalogSupport.idFieldName -> id)

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
                Ok(_).as(ContentTypes.JSON)
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


  private def collectionFind[A](filter: Option[JsValue] = None, sort: Option[JsObject] = None)(implicit r: Reads[A], w: Writes[A]): Future[JsArray] = {
    Logger.debug(s"find query json: $filter")



    val filtered = filter match {
      case Some(filterValue) => collection.find(filterValue)
      case None => collection.genericQueryBuilder
    }

    val sorted = sort match {
      case Some(sorter) => filtered.sort(sorter)
      case None => filtered
    }

    val futureList = sorted.cursor[A].collect[List]()

    futureList.map {
      _.foldLeft(JsArray())((acc, elem) => acc ++ Json.arr(elem))
    }


  }

  private def collectionInsert(entity: JsObject) = {
    import LastErrorWrapperImplicits._
    collection.insert(entity).map {
      lastError => lastError.orFail
    }
  }


  def delete(id: String) = Action.async {
    val query = Json.obj(CatalogSupport.idFieldName -> id)
    val result = collection.remove(query)
    result.map {
      lastError => {
        Logger.debug(s"Successfully deleted with id: $id")
        Ok
      }
    }

  }
}
