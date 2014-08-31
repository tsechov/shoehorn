package models.customer

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime

case class AddressTypeIn(_id: IdType,
                         createdAt: DateTime,
                         lastModifiedAt: DateTime,
                         active: Boolean,
                         description: String,
                         name: String) extends AssetIn with AssetUpdateBuilder[AddressTypeUpdate] {
  override def fillup(lastModifiedAt: DateTime): AddressTypeUpdate = AddressTypeUpdate(lastModifiedAt, active, description, name)
}

object AddressTypeIn extends AssetInCompanion[AddressTypeIn] {
  val collectionName = "addresstypes"
  val format = Json.format[AddressTypeIn]
  type Id=IdType
}


case class AddressTypeUpdate(lastModifiedAt: DateTime,
                             active: Boolean,
                             description: String,
                             name: String) extends AssetUpdate

object AddressTypeUpdate extends AssetUpdateCompanion[AddressTypeUpdate] {
  val format = Json.format[AddressTypeUpdate]
  val collectionName = AddressTypeIn.collectionName


}

case class AddressTypeCreate(active: Boolean,
                             description: String,
                             name: String) extends AssetCreate[AddressTypeIn] {
  override def fillup(b: AssetBase) = AddressTypeIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name)
}

object AddressTypeCreate {
  implicit val reads = Json.reads[AddressTypeCreate]
}


