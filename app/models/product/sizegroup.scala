package models.product

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime

case class SizeGroupIn(_id: IdType,
                       createdAt: DateTime,
                       lastModifiedAt: DateTime,
                       active: Boolean,
                       description: String,
                       from: Int,
                       to: Int) extends AssetIn with AssetUpdateBuilder[SizeGroupUpdate] {
  override def fillup(lastModifiedAt: DateTime): SizeGroupUpdate = SizeGroupUpdate(lastModifiedAt, active, description, from, to)
}

object SizeGroupIn extends AssetInCompanion[SizeGroupIn] {
  val collectionName = "sizegroups"
  val format = Json.format[SizeGroupIn]
}


case class SizeGroupUpdate(lastModifiedAt: DateTime,
                           active: Boolean,
                           description: String,
                           from: Int,
                           to: Int) extends AssetUpdate

object SizeGroupUpdate extends AssetUpdateCompanion[SizeGroupUpdate] {
  val format = Json.format[SizeGroupUpdate]
  val collectionName = SizeGroupIn.collectionName


}

case class SizeGroupCreate(active: Boolean,
                           description: String,
                           from: Int,
                           to: Int) extends AssetCreate[SizeGroupIn] {
  override def fillup(b: AssetBase) = SizeGroupIn(b.id, b.createdAt, b.lastModifiedAt, active, description, from, to)
}

object SizeGroupCreate {
  implicit val reads = Json.reads[SizeGroupCreate]
}


