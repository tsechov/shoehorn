package models.customer

import models.common.Address
import play.api.libs.json.Json

case class Shop(
                 name: String,
                 shopAddress: Address,
                 invoiceAddress: Address,
                 shipmentAddress: Address,
                 status: Boolean
                 )

object Shop {
  implicit val format = Json.format[Shop]
}
