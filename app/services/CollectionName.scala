package services

import play.api.libs.json.JsObject

trait CollectionName[A] {
  def get: String
}

case class DbQuery(query: JsObject, projection: Option[JsObject] = None)



