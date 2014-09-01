package models.customer

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime
import models.common.Referable

case class EmailTypeIn(_id: IdType,
                       createdAt: DateTime,
                       lastModifiedAt: DateTime,
                       active: Boolean,
                       description: String,
                       name: String) extends AssetIn with AssetUpdateBuilder[EmailTypeUpdate] {
  override def fillup(lastModifiedAt: DateTime): EmailTypeUpdate = EmailTypeUpdate(lastModifiedAt, active, description, name)
}

object EmailTypeIn extends AssetInCompanion[EmailTypeIn] with Referable[EmailTypeIn] {
  val collectionName = "emailtypes"
  val format = Json.format[EmailTypeIn]
  type Id = IdType
}


case class EmailTypeUpdate(lastModifiedAt: DateTime,
                           active: Boolean,
                           description: String,
                           name: String) extends AssetUpdate

object EmailTypeUpdate extends AssetUpdateCompanion[EmailTypeUpdate] {
  val format = Json.format[EmailTypeUpdate]
  val collectionName = EmailTypeIn.collectionName


}

case class EmailTypeCreate(active: Boolean,
                           description: String,
                           name: String) extends AssetCreate[EmailTypeIn] {
  override def fillup(b: AssetBase) = EmailTypeIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name)
}

object EmailTypeCreate {
  implicit val reads = Json.reads[EmailTypeCreate]
}


