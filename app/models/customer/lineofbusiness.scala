package models.customer

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime

case class LineOfBusinessIn(_id: IdType,
                            createdAt: DateTime,
                            lastModifiedAt: DateTime,
                            active: Boolean,
                            description: String,
                            name: String) extends AssetIn with AssetUpdateBuilder[LineOfBusinessUpdate] {
  override def fillup(lastModifiedAt: DateTime): LineOfBusinessUpdate = LineOfBusinessUpdate(lastModifiedAt, active, description, name)
}

object LineOfBusinessIn extends AssetInCompanion[LineOfBusinessIn] {
  val collectionName = "lineofbusinesses"
  val format = Json.format[LineOfBusinessIn]
}


case class LineOfBusinessUpdate(lastModifiedAt: DateTime,
                                active: Boolean,
                                description: String,
                                name: String) extends AssetUpdate

object LineOfBusinessUpdate extends AssetUpdateCompanion[LineOfBusinessUpdate] {
  val format = Json.format[LineOfBusinessUpdate]
  val collectionName = LineOfBusinessIn.collectionName


}

case class LineOfBusinessCreate(active: Boolean,
                                description: String,
                                name: String) extends AssetCreate[LineOfBusinessIn] {
  override def fillup(b: AssetBase) = LineOfBusinessIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name)
}

object LineOfBusinessCreate {
  implicit val reads = Json.reads[LineOfBusinessCreate]
}


