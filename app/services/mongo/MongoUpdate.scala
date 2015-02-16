package services.mongo

import models.DateFormatSupport
import org.joda.time.DateTime
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json.obj
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.core.commands.LastError
import play.api.libs.json._

import scala.concurrent.Future

/**
 * @author tsechov
 */
trait MongoUpdate extends DateFormatSupport {
  self: MongoCollection =>
  def update(selector: JsObject, data: JsValue): Future[LastError] = {
    val idPrune = (__ \ '_id).json.prune

    val lastModified = (__ \ 'lastModifiedAt).json.put(dateFormat.writes(new DateTime))

    val augemented = data.transform(idPrune andThen lastModified).get

    collection.update(selector, obj("$set" -> augemented))
  }
}
