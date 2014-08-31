package models.order

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime
import models.AssetBase


case class VariableIn(_id: IdType,
                      createdAt: DateTime,
                      lastModifiedAt: DateTime,
                      active: Boolean,
                      description: String,
                      key: String,
                      value: String) extends AssetIn with AssetUpdateBuilder[VariableUpdate] {
  override def fillup(lastModifiedAt: DateTime): VariableUpdate = VariableUpdate(lastModifiedAt, active, description, key, value)
}

object VariableIn extends AssetInCompanion[VariableIn] {
  val collectionName = "Variables"
  val format = Json.format[VariableIn]
}


case class VariableUpdate(lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          key: String,
                          value: String) extends AssetUpdate

object VariableUpdate extends AssetUpdateCompanion[VariableUpdate] {
  val format = Json.format[VariableUpdate]
  val collectionName = VariableIn.collectionName


}

case class VariableCreate(active: Boolean,
                          description: String,
                          key: String, value: String) extends AssetCreate[VariableIn] {
  override def fillup(b: AssetBase) = VariableIn(b.id, b.createdAt, b.lastModifiedAt, active, description, key, value)
}

object VariableCreate {
  implicit val reads = Json.reads[VariableCreate]
}


