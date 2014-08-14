package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime
import services.CollectionName
import models.AssetSupport.IdType

case class SizeGroup(from: Int, to: Int)

object SizeGroup {
  val reads: Reads[SizeGroup] = ((__ \ "from").read[Int] and (__ \ "to").read[Int])(SizeGroup.apply _)
  val writes: Writes[SizeGroup] = ((__ \ "from").write[Int] and (__ \ "to").write[Int])(unlift(SizeGroup.unapply _))
  implicit val format: Format[SizeGroup] = Format(reads, writes)
}

trait ProductPaths extends AssetPaths {
  val namePath = __ \ "name"
  val itemNumberPath = __ \ "itemNumber"
  val sizeGroupPath = __ \ "sizeGroup"
  val imagePath = __ \ "image"
  val catalogsPath = __ \ "catalogs"
  type ImageUrl = String
  type ItemNumber = String
}

case class ProductCreate(
                          active: Boolean,
                          description: String,
                          name: String,
                          itemNumber: Product.ItemNumber,
                          sizeRanges: List[SizeGroup],
                          image: Product.ImageUrl,
                          catalogs: List[AssetSupport.IdType]
                          ) extends AssetCreate[Product] {
  override def fillup(id: IdType, createdAt: DateTime, lastModifiedAt: DateTime) = Product(id, createdAt, lastModifiedAt, active, description, name, itemNumber, sizeRanges, image, catalogs)
}

object ProductCreate extends ProductPaths {
  implicit val reads: Reads[ProductCreate] =
    (activePath.read[Boolean] and
      descriptionPath.read[String] and
      namePath.read[String] and
      itemNumberPath.read[String] and
      sizeGroupPath.read[List[SizeGroup]] and
      imagePath.read[ImageUrl] and
      catalogsPath.read[List[AssetSupport.IdType]]
      )(ProductCreate.apply _)


}

case class ProductUpdate(
                          lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          name: String,
                          itemNumber: Product.ItemNumber,
                          sizeRanges: List[SizeGroup],
                          image: Product.ImageUrl,
                          catalogs: List[AssetSupport.IdType]
                          )

object ProductUpdate extends ProductPaths with DateFormatSupport {
  val reads: Reads[ProductUpdate] =
    (lastModifiedAtPath.read[DateTime] and
      activePath.read[Boolean] and
      descriptionPath.read[String] and
      namePath.read[String] and
      itemNumberPath.read[String] and
      sizeGroupPath.read[List[SizeGroup]] and
      imagePath.read[ImageUrl] and
      catalogsPath.read[List[AssetSupport.IdType]])(ProductUpdate.apply _)

  val writes: Writes[ProductUpdate] =
    (lastModifiedAtPath.write[DateTime] and
      activePath.write[Boolean] and
      descriptionPath.write[String] and
      namePath.write[String] and
      itemNumberPath.write[String] and
      sizeGroupPath.write[List[SizeGroup]] and
      imagePath.write[ImageUrl] and
      catalogsPath.write[List[AssetSupport.IdType]])(unlift(ProductUpdate.unapply _))

  implicit val format = Format(reads, writes)

  implicit val collectionName = new CollectionName[ProductUpdate] {
    override def get: String = "products"
  }

}

case class Product(
                    id: AssetSupport.IdType,
                    createdAt: DateTime,
                    lastModifiedAt: DateTime,
                    active: Boolean,
                    description: String,
                    name: String,
                    itemNumber: Product.ItemNumber,
                    sizeRanges: List[SizeGroup],
                    image: Product.ImageUrl,
                    catalogs: List[AssetSupport.IdType]) extends AssetUpdate[ProductUpdate] {
  override def fillup(lastModifiedAt: DateTime) = ProductUpdate(lastModifiedAt, active, description, name, itemNumber, sizeRanges, image, catalogs)
}

object Product extends ProductPaths with DateFormatSupport {
  val reads: Reads[Product] = (
    idPath.read[AssetSupport.IdType] and
      createdAtPath.read[DateTime] and
      lastModifiedAtPath.read[DateTime] and
      activePath.read[Boolean] and
      descriptionPath.read[String] and
      namePath.read[String] and
      itemNumberPath.read[String] and
      sizeGroupPath.read[List[SizeGroup]] and
      imagePath.read[ImageUrl] and
      catalogsPath.read[List[AssetSupport.IdType]])(Product.apply _)

  val writes: Writes[Product] = (
    idPath.write[AssetSupport.IdType] and
      createdAtPath.write[DateTime] and
      lastModifiedAtPath.write[DateTime] and
      activePath.write[Boolean] and
      descriptionPath.write[String] and
      namePath.write[String] and
      itemNumberPath.write[String] and
      sizeGroupPath.write[List[SizeGroup]] and
      imagePath.write[ImageUrl] and
      catalogsPath.write[List[AssetSupport.IdType]])(unlift(Product.unapply _))

  implicit val format = Format(reads, writes)

  implicit val collectionName = new CollectionName[Product] {
    override def get: String = "products"
  }
}


