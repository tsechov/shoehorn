package services

import play.api.libs.json.JsObject
import play.api.libs.json.Json

trait CollectionName[A] {
  def get: String
}

case class DbQuery(query: JsObject, projection: Option[JsObject] = None, limit: Option[Int] = None)

object DbQuery {
  def apply(limit: Int): DbQuery = DbQuery(Json.obj(), limit = Some(limit))
}


