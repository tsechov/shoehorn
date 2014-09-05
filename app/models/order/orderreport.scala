package models.order

import models.common.Address
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
                          imageUrl: String,
                          sortiment: List[SortimentItem],
                          totalCount: Int
                          ) {
  if (sortiment.size != 23) throw new IllegalArgumentException("there has to be 22 sortiment items")
}


object ProductReport {

  def apply(modelNumber: String, imageUrl: String, sortiment: List[SortimentItem]): ProductReport = {

    val expandedSortiment = (18 to 40).foldLeft(List[SortimentItem]())((list, pos) => {
      val item = sortiment.find(_.size == pos) match {
        case Some(item) => item
        case None => SortimentItem(pos, 0)
      }
      list :+ item
    })


    ProductReport(modelNumber, imageUrl, expandedSortiment, expandedSortiment.foldLeft(0)((sum, item) => sum + item.count))

  }
}

case class OrderReport(
                        orderId: AssetSupport.IdType,
                        orderNumber: String,
                        lastModifiedAt: DateTime,
                        deadline1: DateTime,
                        deadline2: DateTime,
                        customer: CustomerReport,
                        agent: AgentReport,
                        items: List[ProductReport],
                        sumPrice: Int)


