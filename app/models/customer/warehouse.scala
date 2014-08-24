package models.customer

import models.common.Address
import models.AssetSupport._
import play.api.libs.json.Json
import services.CollectionName
import models.AssetSupport.IdType
import org.joda.time.DateTime
import models._

case class WarehouseIn(
                        _id: IdType,
                        createdAt: DateTime,
                        lastModifiedAt: DateTime,
                        active: Boolean,
                        description: String,
                        name: String,
                        address: Address,
                        status: Boolean,
                        url: UrlType
                        ) extends AssetIn with AssetUpdateBuilder[WarehouseUpdate] {
  override def fillup(lastModifiedAt: DateTime) = WarehouseUpdate(lastModifiedAt, active, description, name, address, status, url)
}

object WarehouseIn extends DateFormatSupport {
  implicit val format = Json.format[WarehouseIn]
  implicit val collectionName = new CollectionName[WarehouseIn] {
    override def get: String = "warehouses"
  }
}

case class WarehouseUpdate(lastModifiedAt: DateTime,
                           active: Boolean,
                           description: String,
                           name: String,
                           address: Address,
                           status: Boolean,
                           url: UrlType) extends AssetUpdate

object WarehouseUpdate extends DateFormatSupport {

  implicit val format = Json.format[WarehouseUpdate]
  implicit val collectionName = new CollectionName[WarehouseUpdate] {
    override def get: String = WarehouseIn.collectionName.get
  }
}

case class WarehouseCreate(active: Boolean,
                           description: String,
                           name: String,
                           address: Address,
                           status: Boolean,
                           url: UrlType) extends AssetCreate[WarehouseIn] {
  override def fillup(id: IdType, createdAt: DateTime, lastModifiedAt: DateTime) = WarehouseIn(id, createdAt, lastModifiedAt, active, description, name, address, status, url)
}

object WarehouseCreate {

  implicit val reads = Json.reads[WarehouseCreate]
}
