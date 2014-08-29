package models.product

import play.api.libs.json._
import org.joda.time.DateTime
import services.CollectionName
import models.AssetSupport.{IdType, UrlType}
import models._
import models.common._


case class CatalogSw(
                      catalogId: IdType,
                      sizeGroupIds: List[IdType],
                      price: Price
                      )

object CatalogSw {
  implicit val format = Json.format[CatalogSw]
}

case class ProductCreate(
                          active: Boolean,
                          description: String,
                          name: String,
                          itemNumber: ProductIn.ItemNumber,
                          image: UrlType,
                          catalogs: List[CatalogSw]
                          ) extends AssetCreate[ProductIn] {
  override def fillup(b: AssetBase) = ProductIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name, itemNumber, image, catalogs)
}

object ProductCreate {
  implicit val reads = Json.reads[ProductCreate]


}

case class ProductUpdate(
                          lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          name: String,
                          itemNumbeAssetSupport: ProductIn.ItemNumber,
                          image: UrlType,
                          catalogs: List[CatalogSw]
                          ) extends AssetUpdate

object ProductUpdate extends DateFormatSupport {


  implicit val format = Json.format[ProductUpdate]

  implicit val collectionName = new CollectionName[ProductUpdate] {
    override def get = ProductIn.collectionName.get
  }

}

case class ProductIn(
                      _id: IdType,
                      createdAt: DateTime,
                      lastModifiedAt: DateTime,
                      active: Boolean,
                      description: String,
                      name: String,
                      itemNumber: ProductIn.ItemNumber,

                      image: UrlType,

                      catalogs: List[CatalogSw]) extends AssetIn with AssetUpdateBuilder[ProductUpdate] {
  override def fillup(lastModifiedAt: DateTime) = ProductUpdate(lastModifiedAt, active, description, name, itemNumber, image, catalogs)
}

object ProductIn extends DateFormatSupport {
  type ItemNumber = String

  implicit val format = Json.format[ProductIn]

  implicit val collectionName = new CollectionName[ProductIn] {
    override def get = "products"
  }
}


