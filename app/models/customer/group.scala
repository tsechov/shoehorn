package models.customer

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime

case class GroupIn(_id: IdType,
                   createdAt: DateTime,
                   lastModifiedAt: DateTime,
                   active: Boolean,
                   description: String,
                   name: String) extends AssetIn with AssetUpdateBuilder[GroupUpdate] {
  override def fillup(lastModifiedAt: DateTime): GroupUpdate = GroupUpdate(lastModifiedAt, active, description, name)
}

object GroupIn extends AssetInCompanion[GroupIn] {
  val collectionName = "groups"
  val format = Json.format[GroupIn]
}


case class GroupUpdate(lastModifiedAt: DateTime,
                       active: Boolean,
                       description: String,
                       name: String) extends AssetUpdate

object GroupUpdate extends AssetUpdateCompanion[GroupUpdate] {
  val format = Json.format[GroupUpdate]
  val collectionName = GroupIn.collectionName


}

case class GroupCreate(active: Boolean,
                       description: String,
                       name: String) extends AssetCreate[GroupIn] {
  override def fillup(b: AssetBase) = GroupIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name)
}

object GroupCreate {
  implicit val reads = Json.reads[GroupCreate]
}


