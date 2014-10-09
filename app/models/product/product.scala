package models.product

import play.api.libs.json._
import org.joda.time.DateTime
import services.CollectionName
import models.AssetSupport.{IdType, UrlType}
import models._


case class PricedSizeGroup(sizeGroupId: IdType, unitPrice: Int)

object PricedSizeGroup {
  implicit val format = Json.format[PricedSizeGroup]
}

case class CatalogSw(
                      catalogId: IdType,
                      sizeGroups: List[PricedSizeGroup]
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
                          imageThumb: UrlType,
                          catalogs: List[CatalogSw]
                          ) extends AssetCreate[ProductIn] {
  override def fillup(b: AssetBase) = ProductIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name, itemNumber, image, imageThumb, catalogs)
}

object ProductCreate {
  implicit val reads = Json.reads[ProductCreate]


}

case class ProductUpdate(
                          lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          name: String,
                          itemNumber: ProductIn.ItemNumber,
                          image: UrlType,
                          imageThumb: UrlType,
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
                      imageThumb: UrlType,

                      catalogs: List[CatalogSw]) extends AssetIn with AssetUpdateBuilder[ProductUpdate] {
  override def fillup(lastModifiedAt: DateTime) = ProductUpdate(lastModifiedAt, active, description, name, itemNumber, image, imageThumb, catalogs)
}

object ProductIn extends DateFormatSupport {
  type ItemNumber = String

  implicit val format = Json.format[ProductIn]

  implicit val collectionName = new CollectionName[ProductIn] {
    override def get = "products"
  }
}


