package models.customer

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime

case class ContactTypeIn(_id: IdType,
                         createdAt: DateTime,
                         lastModifiedAt: DateTime,
                         active: Boolean,
                         description: String,
                         name: String) extends AssetIn with AssetUpdateBuilder[ContactTypeUpdate] {
  override def fillup(lastModifiedAt: DateTime): ContactTypeUpdate = ContactTypeUpdate(lastModifiedAt, active, description, name)


}

object ContactTypeIn extends AssetInCompanion[ContactTypeIn] {
  val collectionName = "contacttypes"
  val format = Json.format[ContactTypeIn]

}


case class ContactTypeUpdate(lastModifiedAt: DateTime,
                             active: Boolean,
                             description: String,
                             name: String) extends AssetUpdate

object ContactTypeUpdate extends AssetUpdateCompanion[ContactTypeUpdate] {
  val format = Json.format[ContactTypeUpdate]
  val collectionName = ContactTypeIn.collectionName


}

case class ContactTypeCreate(active: Boolean,
                             description: String,
                             name: String) extends AssetCreate[ContactTypeIn] {
  override def fillup(b: AssetBase) = ContactTypeIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name)
}

object ContactTypeCreate {
  implicit val reads = Json.reads[ContactTypeCreate]
}


