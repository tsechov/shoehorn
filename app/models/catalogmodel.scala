package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime


case class CatalogCreate(
                          active: Boolean,
                          description: String,
                          year: Int,
                          season: String,
                          status: Boolean,
                          webStatus: Boolean
                          )

object CatalogCreate {
  implicit val reads: Reads[CatalogCreate] =
    ((__ \ "active").read[Boolean] and
      (__ \ "description").read[String] and
      (__ \ "year").read[Int] and
      (__ \ "season").read[String] and
      (__ \ "status").read[Boolean] and
      (__ \ "webStatus").read[Boolean])(CatalogCreate.apply _)



  def transformer(json: JsValue): (String, DateTime) => Reads[JsObject] = {
    (id, date) => {
      (__).json.update(__.read[JsObject].map {
        root =>
          root ++ Json.obj("_id" -> id, "createdAt" -> date, "lastModifiedAt" -> date)
      })
    }
  }
}


case class CatalogUpdate(
                          _id: Catalog.IdType,
                          active: Boolean,
                          description: String,
                          year: Int,
                          season: String,
                          status: Boolean,
                          webStatus: Boolean
                          )

object CatalogUpdate {
  implicit val reads: Reads[CatalogUpdate] =
    ((__ \ "_id").read[String] and
      (__ \ "active").read[Boolean] and
      (__ \ "description").read[String] and
      (__ \ "year").read[Int] and
      (__ \ "season").read[String] and
      (__ \ "status").read[Boolean] and
      (__ \ "webStatus").read[Boolean])(CatalogUpdate.apply _)
}



