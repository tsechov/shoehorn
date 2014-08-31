package models.catalog

import play.api.libs.json._

import org.joda.time.DateTime
import services.CollectionName
import models.AssetSupport._
import models._


case class CatalogCreate(
                          active: Boolean,
                          description: String,
                          year: Int,
                          season: String,
                          status: Boolean,
                          webStatus: Boolean,
                          openingTime: DateTime,
                          closingTime: DateTime
                          ) extends AssetCreate[CatalogIn] {
  def fillup(b: AssetBase) = CatalogIn(b.id, b.createdAt, b.lastModifiedAt, active, description, year, season, status, webStatus, openingTime: DateTime,
    closingTime: DateTime)
}

object CatalogCreate extends DateFormatSupport {
  implicit val reads = Json.reads[CatalogCreate]
}


case class CatalogUpdate(
                          lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          year: Int,
                          season: String,
                          status: Boolean,
                          webStatus: Boolean,
                          openingTime: DateTime,
                          closingTime: DateTime
                          ) extends AssetUpdate

object CatalogUpdate extends DateFormatSupport {


  implicit val format = Json.format[CatalogUpdate]
  implicit val collectionName = new CollectionName[CatalogUpdate] {
    override def get: String = CatalogIn.collectionName.get
  }


}

case class CatalogIn(
                      _id: IdType,
                      createdAt: DateTime,
                      lastModifiedAt: DateTime,
                      active: Boolean,
                      description: String,
                      year: Int,
                      season: String,
                      status: Boolean,
                      webStatus: Boolean,
                      openingTime: DateTime,
                      closingTime: DateTime
                      ) extends AssetIn with AssetUpdateBuilder[CatalogUpdate] {
  override def fillup(lastModifiedAt: DateTime): CatalogUpdate = CatalogUpdate(lastModifiedAt, active, description, year, season, status, webStatus, openingTime: DateTime,
    closingTime: DateTime)
}

object CatalogIn extends DateFormatSupport {


  implicit val formats = Json.format[CatalogIn]

  implicit val collectionName = new CollectionName[CatalogIn] {
    override def get: String = "catalogs"
  }

}







