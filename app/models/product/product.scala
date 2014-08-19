package models.product

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime
import services.CollectionName
import models.AssetSupport.{IdType, UrlType}
import models.{AssetUpdate, DateFormatSupport, AssetCreate, AssetPaths}
import models.common._


case class SizeGroup(from: Int, to: Int)

object SizeGroup {
  implicit val format = Json.format[SizeGroup]
}


case class CatalogSw(
                      catalogId: IdType,
                      sizeGroupIds: List[IdType],
                      price: Price
                      )

object CatalogSw {
  implicit val format = Json.format[CatalogSw]
}

trait ProductPaths extends AssetPaths {
  val namePath = __ \ "name"
  val itemNumberPath = __ \ "itemNumber"
  val imagePath = __ \ "image"
  val catalogsPath = __ \ "catalogs"

  type ItemNumber = String
}

case class ProductCreate(
                          active: Boolean,
                          description: String,
                          name: String,
                          itemNumber: Product.ItemNumber,
                          image: UrlType,
                          catalogs: List[CatalogSw]
                          ) extends AssetCreate[Product] {
  override def fillup(id: IdType, createdAt: DateTime, lastModifiedAt: DateTime) = Product(id, createdAt, lastModifiedAt, active, description, name, itemNumber, image, catalogs)
}

object ProductCreate extends ProductPaths {
  implicit val reads: Reads[ProductCreate] =
    (activePath.read[Boolean] and
      descriptionPath.read[String] and
      namePath.read[String] and
      itemNumberPath.read[String] and
      imagePath.read[UrlType] and
      catalogsPath.read[List[CatalogSw]]
      )(ProductCreate.apply _)


}

case class ProductUpdate(
                          lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          name: String,
                          itemNumber: Product.ItemNumber,
                          image: UrlType,
                          catalogs: List[CatalogSw]
                          )

object ProductUpdate extends ProductPaths with DateFormatSupport {
  val reads: Reads[ProductUpdate] =
    (lastModifiedAtPath.read[DateTime] and
      activePath.read[Boolean] and
      descriptionPath.read[String] and
      namePath.read[String] and
      itemNumberPath.read[String] and
      imagePath.read[UrlType] and
      catalogsPath.read[List[CatalogSw]])(ProductUpdate.apply _)

  val writes: Writes[ProductUpdate] =
    (lastModifiedAtPath.write[DateTime] and
      activePath.write[Boolean] and
      descriptionPath.write[String] and
      namePath.write[String] and
      itemNumberPath.write[String] and
      imagePath.write[UrlType] and
      catalogsPath.write[List[CatalogSw]])(unlift(ProductUpdate.unapply _))

  implicit val format = Format(reads, writes)

  implicit val collectionName = new CollectionName[ProductUpdate] {
    override def get: String = Product.collectionName.get
  }

}

case class Product(
                    id: IdType,
                    createdAt: DateTime,
                    lastModifiedAt: DateTime,
                    active: Boolean,
                    description: String,
                    name: String,
                    itemNumber: Product.ItemNumber,

                    image: UrlType,

                    catalogs: List[CatalogSw]) extends AssetUpdate[ProductUpdate] {
  override def fillup(lastModifiedAt: DateTime) = ProductUpdate(lastModifiedAt, active, description, name, itemNumber, image, catalogs)
}

object Product extends ProductPaths with DateFormatSupport {
  val reads: Reads[Product] = (
    idPath.read[IdType] and
      createdAtPath.read[DateTime] and
      lastModifiedAtPath.read[DateTime] and
      activePath.read[Boolean] and
      descriptionPath.read[String] and
      namePath.read[String] and
      itemNumberPath.read[String] and
      imagePath.read[UrlType] and
      catalogsPath.read[List[CatalogSw]])(Product.apply _)

  val writes: Writes[Product] = (
    idPath.write[IdType] and
      createdAtPath.write[DateTime] and
      lastModifiedAtPath.write[DateTime] and
      activePath.write[Boolean] and
      descriptionPath.write[String] and
      namePath.write[String] and
      itemNumberPath.write[String] and
      imagePath.write[UrlType] and
      catalogsPath.write[List[CatalogSw]])(unlift(Product.unapply _))

  implicit val format = Format(reads, writes)

  implicit val collectionName = new CollectionName[Product] {
    override def get: String = "products"
  }
}


