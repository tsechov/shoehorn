package models.order

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime

case class DeadlineTypeIn(_id: IdType,
                          createdAt: DateTime,
                          lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          `type`: String) extends AssetIn with AssetUpdateBuilder[DeadlineTypeUpdate] {
  override def fillup(lastModifiedAt: DateTime): DeadlineTypeUpdate = DeadlineTypeUpdate(lastModifiedAt, active, description, `type`)
}

object DeadlineTypeIn extends AssetInCompanion[DeadlineTypeIn] {
  val collectionName = "deadlinetypes"
  val format = Json.format[DeadlineTypeIn]
}


case class DeadlineTypeUpdate(lastModifiedAt: DateTime,
                              active: Boolean,
                              description: String,
                              `type`: String) extends AssetUpdate

object DeadlineTypeUpdate extends AssetUpdateCompanion[DeadlineTypeUpdate] {
  val format = Json.format[DeadlineTypeUpdate]
  val collectionName = DeadlineTypeIn.collectionName


}

case class DeadlineTypeCreate(active: Boolean,
                              description: String,
                              `type`: String) extends AssetCreate[DeadlineTypeIn] {
  override def fillup(b: AssetBase) = DeadlineTypeIn(b.id, b.createdAt, b.lastModifiedAt, active, description, `type`)
}

object DeadlineTypeCreate {
  implicit val reads = Json.reads[DeadlineTypeCreate]
}


