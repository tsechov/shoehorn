package models.customer

import models.common.Address
import models.AssetSupport._
import play.api.libs.json.Json

case class Warehouse(
                      name: String,
                      address: Address,
                      status: Boolean,
                      url: UrlType
                      )

object Warehouse {
  implicit val format = Json.format[Warehouse]
}
