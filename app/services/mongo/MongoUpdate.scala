package services.mongo

import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json.obj
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.core.commands.LastError

import scala.concurrent.Future

/**
 * @author tsechov
 */
trait MongoUpdate {
  self: MongoCollection =>
  def update(selector: JsObject, data: JsValue): Future[LastError] = {
    collection.update(selector, obj("$set" -> data))
  }
}
