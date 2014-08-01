package models

package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime

case class SizeRange(from: Int, to: Int)

object SizeRange {
  val reads: Reads[SizeRange] = ((__ \ "from").read[Int] and (__ \ "to").read[Int])(SizeRange.apply _)
  val writes: Writes[SizeRange] = ((__ \ "from").write[Int] and (__ \ "to").write[Int])(unlift(SizeRange.unapply _))
  implicit val format: Format[SizeRange] = Format(reads, writes)
}

trait ModelPaths extends AssetPaths {

  val itemNumberPath = __ \ "itemNumber"
  val sizeRangesPath = __ \ "sizeRanges"
  val imagePath = __ \ "image"
  val catalogsPath = __ \ "catalogs"
  type ImageUrl = String
  type ItemNumber = String
}

case class ModelCreate(
                        active: Boolean,
                        description: String,
                        itemNumber: Model.ItemNumber,
                        sizeRanges: List[SizeRange],
                        image: Model.ImageUrl,
                        catalogs: List[AssetSupport.IdType]
                        )

object ModelCreate extends ModelPaths {
  implicit val r: Reads[ModelCreate] =
    (activePath.read[Boolean] and
      descriptionPath.read[String] and
      itemNumberPath.read[String] and
      sizeRangesPath.read[List[SizeRange]] and
      imagePath.read[ImageUrl] and
      catalogsPath.read[List[AssetSupport.IdType]]
      )(ModelCreate.apply _)


}

case class ModelUpdate(
                        lastModifiedAt: DateTime,
                        active: Boolean,
                        description: String,
                        itemNumber: Model.ItemNumber,
                        sizeRanges: List[SizeRange],
                        image: Model.ImageUrl,
                        catalogs: List[AssetSupport.IdType]
                        )

object ModelUpdate extends ModelPaths with DateFormatSupport {
  implicit val r: Reads[ModelUpdate] =
    (lastModifiedAtPath.read[DateTime] and
      activePath.read[Boolean] and
      descriptionPath.read[String] and
      itemNumberPath.read[String] and
      sizeRangesPath.read[List[SizeRange]] and
      imagePath.read[ImageUrl] and
      catalogsPath.read[List[AssetSupport.IdType]])(ModelUpdate.apply _)

}

case class Model(
                  id: AssetSupport.IdType,
                  createdAt: DateTime,
                  lastModifiedAt: DateTime,
                  active: Boolean,
                  description: String,
                  itemNumber: Model.ItemNumber,
                  sizeRanges: List[SizeRange],
                  image: Model.ImageUrl,
                  catalogs: List[AssetSupport.IdType])

object Model extends ModelPaths with DateFormatSupport {
  val r: Reads[Model] = (
    idPath.read[AssetSupport.IdType] and
      createdAtPath.read[DateTime] and
      lastModifiedAtPath.read[DateTime] and
      activePath.read[Boolean] and
      descriptionPath.read[String] and
      itemNumberPath.read[String] and
      sizeRangesPath.read[List[SizeRange]] and
      imagePath.read[ImageUrl] and
      catalogsPath.read[List[AssetSupport.IdType]])(Model.apply _)

  val w: Writes[Model] = (
    idPath.write[AssetSupport.IdType] and
    createdAtPath.write[DateTime] and
    lastModifiedAtPath.write[DateTime] and
    activePath.write[Boolean] and
    descriptionPath.write[String] and
    itemNumberPath.write[String] and
    sizeRangesPath.write[List[SizeRange]] and
    imagePath.write[ImageUrl] and
    catalogsPath.write[List[AssetSupport.IdType]])(unlift(Model.unapply _))

  implicit val format = Format(r, w)
}


