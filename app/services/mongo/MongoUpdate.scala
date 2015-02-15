package services.mongo

import models.DateFormatSupport
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
    val transformer = (__ \ '_id).json.prune
    val woId = data.transform(transformer).get
    dateFormat
    val jsonTransformer = (__ \ 'lastModifiedAt).json.put(JsNumber(456))
    collection.update(selector, obj("$set" -> woId))
  }
}
