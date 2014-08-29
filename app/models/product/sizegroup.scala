package models.product

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime
import services.CollectionName

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

object SizeGroupUpdate {
  implicit val format = Json.format[SizeGroupUpdate]
  implicit val collectionName = new CollectionName[SizeGroupUpdate] {
    override def get = SizeGroupIn.cn.get
  }
}

case class SizeGroupCreate(active: Boolean,
                           description: String,
                           from: Int,
                           to: Int) extends AssetCreate[SizeGroupIn] {
  override def fillup(id: AssetSupport.IdType, createdAt: DateTime, lastModifiedAt: DateTime) = SizeGroupIn(id, createdAt, lastModifiedAt, active, description, from, to)
}

object SizeGroupCreate {
  implicit val reads = Json.reads[SizeGroupCreate]
}


