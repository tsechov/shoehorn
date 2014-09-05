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


case class AgentReport(
                        name: String,
                        payment: String,
                        phone: String = "",
                        mobile: String = "",
                        fax: String = "",
                        email: String
                        )


case class SortimentItem(
                          size: Int,
                          count: Int
                          )


case class ProductReport(
                          modelNumber: String,
                          color: String,
                          sortiment: List[SortimentItem],
                          totalCount: Int,
                          netPrice: Int,
                          shippingDeadline: DateTime
                          ) {
  println(sortiment)
  if (sortiment.size != 23) throw new IllegalArgumentException("there has to be 22 sortiment items")
}


object ProductReport {

  def apply(modelNumber: String, color: String, sortiment: List[SortimentItem], netPrice: Int, shippingDeadline: DateTime): ProductReport = {

    val expandedSortiment = (18 to 40).foldLeft(List[SortimentItem]())((list, pos) => {
      val item = sortiment.find(_.size == pos) match {
        case Some(item) => item
        case None => SortimentItem(pos, 0)
      }
      list :+ item
    })


    ProductReport(modelNumber, color, expandedSortiment, expandedSortiment.foldLeft(0)((sum, item) => sum + item.count), netPrice, shippingDeadline)

  }
}

case class OrderReport(
                        orderId: AssetSupport.IdType,
                        orderNumber: String,
                        lastModifiedAt: DateTime,
                        customer: CustomerReport,
                        agent: AgentReport,
                        items: List[ProductReport])


