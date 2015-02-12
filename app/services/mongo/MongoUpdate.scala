package services.mongo

import play.api.libs.json.JsObject
import play.api.libs.json.Json.obj
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * @author tsechov
 */
trait MongoUpdate {
  self: MongoCollection =>
  def update(selector: JsObject, data: JsObject) = {
    collection.update(selector, obj("$set" -> data))
  }
}
