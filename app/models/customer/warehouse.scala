package models.customer

import models.common.Address
import models.AssetSupport._
import play.api.libs.json.Json
import services.CollectionName
import models.AssetSupport.IdType
import org.joda.time.DateTime
import models.{AssetCreate, AssetUpdate}

case class Warehouse(
                      id: IdType,
                      createdAt: DateTime,
                      lastModifiedAt: DateTime,
                      active: Boolean,
                      description: String,
                      name: String,
                      address: Address,
                      status: Boolean,
                      url: UrlType
                      ) extends AssetUpdate[WarehouseUpdate] {
  override def fillup(lastModifiedAt: DateTime): WarehouseUpdate = WarehouseUpdate(lastModifiedAt, active, description, name, address, status, url)
}

object Warehouse {
  implicit val format = Json.format[Warehouse]
  implicit val collectionName = new CollectionName[Warehouse] {
    override def get: String = "warehouses"
  }
}

case class WarehouseUpdate(lastModifiedAt: DateTime,
                           active: Boolean,
                           description: String,
                           name: String,
                           address: Address,
                           status: Boolean,
                           url: UrlType)

object WarehouseUpdate {

  implicit val format = Json.format[WarehouseUpdate]
  implicit val collectionName = new CollectionName[WarehouseUpdate] {
    override def get: String = Warehouse.collectionName.get
  }
}

case class WarehouseCreate(active: Boolean,
                           description: String,
                           name: String,
                           address: Address,
                           status: Boolean,
                           url: UrlType) extends AssetCreate[Warehouse] {
  override def fillup(id: IdType, createdAt: DateTime, lastModifiedAt: DateTime) = Warehouse(id, createdAt, lastModifiedAt, active, description, name, address, status, url)
}

object WarehouseCreate {

  implicit val reads = Json.reads[WarehouseCreate]
}
