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
import javax.management.relation.InvalidRelationIdException


@Singleton
class Catalogs extends Controller with MongoController with CatalogPaths{

  def collection: JSONCollection = db.collection[JSONCollection]("catalogs")

  private val contextUrl = Play.application(play.api.Play.current).configuration.getString("context.url").getOrElse("")

  def locationUrl(id: String) = contextUrl + controllers.routes.Catalogs.getById(id).toString

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
      } yield transformed

      result.map {
        entity =>
          collection.insert(entity).map {
            lastError =>
              Logger.debug(s"Successfully inserted with id: $id")
              Created.as(ContentTypes.JSON)
                .withHeaders(HeaderNames.LOCATION -> locationUrl(id))
          }
      }.recoverTotal(error => {
        val jsonError=JsError.toFlatJson(error)
        Logger.debug("invalid input json for create: " + prettyPrint(jsonError))
        Future.successful(BadRequest(jsonError))
      })
  }

  def update(id:Catalog.IdType) = Action.async(parse.json) {
    request =>
      val json = request.body
      val now = new DateTime()
      val transformer = CatalogUpdate.transformer(json)(now)
      val result = for {
        transformed <- {println("transform :"+json);json.transform(transformer)}
        validated <- {println("validate: "+transformed);transformed.validate[CatalogUpdate]}

      } yield (validated,transformed)

      result.map {
        entity => {
          val selector = obj(CatalogSupport.idFieldName->id)

          val modifier = obj("$set" -> entity._2)
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

  def getById(id: Catalog.IdType) = Action.async {
    val query = obj(CatalogSupport.idFieldName -> id)

    val cursor = collection.find(query).cursor[Catalog].collect[List]()
    val futureJson = cursor.map {
      list => list match {
        case head :: _ => Some(toJson(head))
        case Nil => None
      }
    }
    futureJson.map(jsObjectOpt => jsObjectOpt match {
      case Some(jsObject) => Ok(jsObject)
      case None => NotFound
    })
  }

  def find(queryString:Option[String]) = Action.async {
    request =>
      Logger.debug(s"find queryString: $queryString")

      val query = for {
        q<-request.getQueryString("q") match {case Some(value) => Success(value);case None => Failure(new IllegalArgumentException("blah"))}
        json <- Try(Json.parse(q))

      } yield json
      Logger.debug(s"find query json: $query")
    Logger.debug(Json.prettyPrint(query.get))
      import CatalogSupport._
      val cursor: Cursor[Catalog] =
        collection
          .find(query.get)
          .sort(obj(createdAtFieldName -> -1))
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

  }


  def delete(id: String) = Action.async {
    val query = Json.obj(CatalogSupport.idFieldName -> id)
    val result=collection.remove(query)
    result.map{
      lastError =>
      {
        Logger.debug(s"Successfully deleted with id: $id")
        Ok
      }
    }

  }
}
