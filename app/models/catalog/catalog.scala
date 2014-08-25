package models.catalog

import play.api.libs.json._

import org.joda.time.DateTime
import services.CollectionName
import models.AssetSupport._
import models.{AssetUpdateBuilder, DateFormatSupport, AssetPaths, AssetCreate}


case class CatalogCreate(
                          active: Boolean,
                          description: String,
                          year: Int,
                          season: String,
                          status: Boolean,
                          webStatus: Boolean
                          ) extends AssetCreate[Catalog] {
  def fillup(id: IdType, createdAt: DateTime, lastModifiedAt: DateTime) = Catalog(id, createdAt, lastModifiedAt, active, description, year, season, status, webStatus)
}

object CatalogCreate {
  implicit val reads = Json.reads[CatalogCreate]
}


case class CatalogUpdate(
                          lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          year: Int,
                          season: String,
                          status: Boolean,
                          webStatus: Boolean
                          )

object CatalogUpdate extends DateFormatSupport {


  implicit val format = Json.format[CatalogUpdate]
  implicit val collectionName = new CollectionName[CatalogUpdate] {
    override def get: String = Catalog.collectionName.get
  }


}

case class Catalog(
                    id: IdType,
                    createdAt: DateTime,
                    lastModifiedAt: DateTime,
                    active: Boolean,
                    description: String,
                    year: Int,
                    season: String,
                    status: Boolean,
                    webStatus: Boolean
                    ) extends AssetUpdateBuilder[CatalogUpdate] {
  override def fillup(lastModifiedAt: DateTime): CatalogUpdate = CatalogUpdate(lastModifiedAt, active, description, year, season, status, webStatus)
}

object Catalog extends DateFormatSupport {


  implicit val formats = Json.format[Catalog]

  implicit val collectionName = new CollectionName[Catalog] {
    override def get: String = "catalogs"
  }

}







