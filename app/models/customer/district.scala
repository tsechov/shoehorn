package models.customer

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime

case class DistrictIn(_id: IdType,
                      createdAt: DateTime,
                      lastModifiedAt: DateTime,
                      active: Boolean,
                      description: String,
                      name: String) extends AssetIn with AssetUpdateBuilder[DistrictUpdate] {
  override def fillup(lastModifiedAt: DateTime): DistrictUpdate = DistrictUpdate(lastModifiedAt, active, description, name)
}

object DistrictIn extends AssetInCompanion[DistrictIn] {
  val collectionName = "districts"
  val format = Json.format[DistrictIn]
}


case class DistrictUpdate(lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          name: String) extends AssetUpdate

object DistrictUpdate extends AssetUpdateCompanion[DistrictUpdate] {
  val format = Json.format[DistrictUpdate]
  val collectionName = DistrictIn.collectionName


}

case class DistrictCreate(active: Boolean,
                          description: String,
                          name: String) extends AssetCreate[DistrictIn] {
  override def fillup(b: AssetBase) = DistrictIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name)
}

object DistrictCreate {
  implicit val reads = Json.reads[DistrictCreate]
}


