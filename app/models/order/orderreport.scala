package models.order

import models.common.Address
import play.api.libs.json.Json
import org.joda.time.DateTime
import models.AssetSupport

case class CustomerReport(
                           name: String,
                           address: Address,
                           shippingAddress: Address,
                           taxExemptNumber: String,
                           bankAccountNumber: String
                           )

object CustomerReport {
  implicit val format = Json.format[CustomerReport]
}

case class AgentReport(
                        name: String,
                        payment: String,
                        phone: Option[String],
                        mobile: Option[String],
                        fax: Option[String],
                        email: Option[String]
                        )

object AgentReport {
  implicit val format = Json.format[AgentReport]
}

case class SortimentItem(
                          size: Int,
                          count: Int
                          )

object SortimentItem {
  implicit val format = Json.format[SortimentItem]
}

case class ProductReport(
                          modelNumber: String,
                          color: String,
                          sortiment: List[SortimentItem],
                          totalCount: Int,
                          netPrice: Int,
                          shippingDeadline: DateTime
                          )

object ProductReport {
  implicit val format = Json.format[ProductReport]
}

case class OrderReport(
                        orderId: AssetSupport.IdType,
                        orderNumber: String,
                        lastModifiedAt: DateTime,
                        customer: CustomerReport,
                        agent: AgentReport,
                        items: ProductReport
                        )

object OrderReport {
  implicit val format = Json.format[OrderReport]
}