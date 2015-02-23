package models.customer

import models.AssetSupport._
import models._
import models.common.Address
import org.joda.time.DateTime
import play.api.libs.json.Json
import services.CollectionName

case class ShopIn(
                   _id: IdType,
                   createdAt: DateTime,
                   lastModifiedAt: DateTime,
                   active: Boolean,
                   description: String,
                   name: String,
                   status: Boolean,
                   website: Option[String],
                   address: Address
                   ) extends AssetIn with AssetUpdateBuilder[ShopUpdate] {
  override def fillup(lastModifiedAt: DateTime) = ShopUpdate(lastModifiedAt, active, description, name, status, website, address)
}

object ShopIn extends DateFormatSupport {
  implicit val format = Json.format[ShopIn]
  implicit val collectionName = new CollectionName[ShopIn] {
    override def get: String = "shops"
  }


}

case class ShopUpdate(lastModifiedAt: DateTime,
                      active: Boolean,
                      description: String,
                      name: String,
                      status: Boolean,
                      website: Option[String],
                      address: Address) extends AssetUpdate

object ShopUpdate extends DateFormatSupport {

  implicit val format = Json.format[ShopUpdate]
  implicit val collectionName = new CollectionName[ShopUpdate] {
    override def get: String = ShopIn.collectionName.get
  }
}

case class ShopCreate(active: Boolean,
                      description: String,
                      name: String,
                      status: Boolean,
                      website: Option[String],
                      address: Address) extends AssetCreate[ShopIn] {
  override def fillup(b: AssetBase) = ShopIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name, status, website, address)

}

object ShopCreate {
  implicit val reads = Json.reads[ShopCreate]
}
