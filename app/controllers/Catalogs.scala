package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{LoggerFactory, Logger}
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID

import play.api.http.{HeaderNames, ContentTypes}
import org.joda.time.DateTime

import play.api.Play


@Singleton
class Catalogs extends Controller with MongoController {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Catalogs])


  def collection: JSONCollection = db.collection[JSONCollection]("catalogs")


  import models.Catalog


  def create = Action.async(parse.json) {
    request =>
      val json = request.body
      val id = BSONObjectID.generate.stringify
      val now = new DateTime()
      val transformer = Catalog.createTransformer(json)(id, now)
      val result = for {
        transformed <- json.transform(transformer)
        validated <- transformed.validate[Catalog]
      } yield validated

      result.map {
        entity =>
          collection.insert(entity).map {
            lastError =>
              logger.debug(s"Successfully inserted with id: $id")
              val location = Play.application(play.api.Play.current).configuration.getString("context.url").getOrElse("") + controllers.routes.Catalogs.getById(id).toString
              CreatedResponse(location)
          }
      }.recoverTotal(error => {
        logger.debug("invalid input json: "+JsError.toFlatJson(error))
        Future.successful(BadRequest(JsError.toFlatJson(error)))
      })
  }

  def getById(id: String) = Action.async {
    val query = Json.obj(models.AssetModelSupport.idFieldName -> id)

    val cursor = collection.find(query).cursor[Catalog].collect[List]()
    val futureJson = cursor.map {
      list => list match {
        case head :: _ => Json.toJson(head)
        case Nil => Json.obj()
      }
    }
    futureJson.map(jsObject => Ok(jsObject))
  }

  def find = Action.async {

    val cursor: Cursor[Catalog] =
      collection
        .find(Json.obj("active" -> true))
        .sort(Json.obj("createdAt" -> -1))
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
      .withHeaders(HeaderNames.LOCATION ->location)
  }


}
