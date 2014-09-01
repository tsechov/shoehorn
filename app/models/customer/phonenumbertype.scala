package models.customer

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime
import models.common.Referable

case class PhoneNumberTypeIn(_id: IdType,
                             createdAt: DateTime,
                             lastModifiedAt: DateTime,
                             active: Boolean,
                             description: String,
                             name: String) extends AssetIn with AssetUpdateBuilder[PhoneNumberTypeUpdate] {
  override def fillup(lastModifiedAt: DateTime): PhoneNumberTypeUpdate = PhoneNumberTypeUpdate(lastModifiedAt, active, description, name)
}

object PhoneNumberTypeIn extends AssetInCompanion[PhoneNumberTypeIn] with Referable[PhoneNumberTypeIn] {
  val collectionName = "phonenumbertypes"
  val format = Json.format[PhoneNumberTypeIn]
  type Id = IdType
}


case class PhoneNumberTypeUpdate(lastModifiedAt: DateTime,
                                 active: Boolean,
                                 description: String,
                                 name: String) extends AssetUpdate

object PhoneNumberTypeUpdate extends AssetUpdateCompanion[PhoneNumberTypeUpdate] {
  val format = Json.format[PhoneNumberTypeUpdate]
  val collectionName = PhoneNumberTypeIn.collectionName


}

case class PhoneNumberTypeCreate(active: Boolean,
                                 description: String,
                                 name: String) extends AssetCreate[PhoneNumberTypeIn] {
  override def fillup(b: AssetBase) = PhoneNumberTypeIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name)
}

object PhoneNumberTypeCreate {
  implicit val reads = Json.reads[PhoneNumberTypeCreate]
}


