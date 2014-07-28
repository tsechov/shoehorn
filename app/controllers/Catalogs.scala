package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID

import play.api.http.{HeaderNames, ContentTypes}
import org.joda.time.DateTime

import play.api.{Logger, Play}
import models.{CatalogPaths, CatalogUpdate, CatalogSupport, CatalogCreate}


@Singleton
class Catalogs extends Controller with MongoController with CatalogPaths{

  def collection: JSONCollection = db.collection[JSONCollection]("catalogs")

  private val contextUrl = Play.application(play.api.Play.current).configuration.getString("context.url").getOrElse("")

  def locationUrl(id: String) = contextUrl + controllers.routes.Catalogs.getById(id).toString

  import models.Catalog

  def create = CorsAction.async(parse.json) {
    request =>
      val json = request.body
      val id = BSONObjectID.generate.stringify
      val now = new DateTime()
      val transformer = CatalogCreate.transformer(json)(id, now)

      val result = for {
        validated <- json.validate[CatalogCreate]
        transformed <- json.transform(transformer)
      } yield transformed

      result.map {
        entity =>
          collection.insert(entity).map {
            lastError =>
              Logger.debug(s"Successfully inserted with id: $id")
              CreatedResponse(locationUrl(id))
          }
      }.recoverTotal(error => {
        Logger.debug("invalid input json for create: " + JsError.toFlatJson(error))
        Future.successful(BadRequest(JsError.toFlatJson(error)))
      })
  }

  def update(id:Catalog.IdType) = CorsAction.async(parse.json) {
    request =>
      val json = request.body
      val now = new DateTime()
      val transformer = CatalogUpdate.transformer(json)(now)
      val result = for {
        validated <- {println("validate: "+json);json.validate[CatalogCreate]}
        transformed <- {println("transform :"+json);json.transform(transformer)}
      } yield transformed

      result.map {
        entity => {
          val selector = Json.obj(CatalogSupport.idFieldName->id)

          val modifier = Json.obj("$set" -> entity)
          collection.update(selector,modifier).map {
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

  def getById(id: Catalog.IdType) = CorsAction.async {
    val query = Json.obj(CatalogSupport.idFieldName -> id)

    val cursor = collection.find(query).cursor[Catalog].collect[List]()
    val futureJson = cursor.map {
      list => list match {
        case head :: _ => Json.toJson(head)
        case Nil => Json.obj()
      }
    }
    futureJson.map(jsObject => Ok(jsObject))
  }

  def find = CorsAction.async {
    import CatalogSupport._
    val cursor: Cursor[Catalog] =
      collection
        .find(Json.obj(activeFieldName -> true))
        .sort(Json.obj(createdAtFieldName -> -1))
        .cursor[Catalog]

    val futureList: Future[List[Catalog]] = cursor.collect[List]()

    val futureJsonArray: Future[JsArray] = futureList.map {
      entities => {
        entities.foldLeft(JsArray())((acc, elem) => acc ++ Json.arr(elem))
      }
    }

    futureJsonArray.map {
      jsArray => {
        Ok(jsArray).as(ContentTypes.JSON)
      }
    }
  }

  private def CreatedResponse(location: String) = {
    Created(ContentTypes.JSON)
      .withHeaders(HeaderNames.LOCATION -> location)
  }


}
