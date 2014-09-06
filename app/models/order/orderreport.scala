package models.order

import models.AssetSupport
import org.joda.time.DateTime

case class CustomerReport(
                           name: String,
                           address: String,
                           shippingName: String,
                           shippingAddress: String,
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
                        lastModifiedAt: String,
                        deadline1: String,
                        deadline2: String,
                        deadline3: String,
                        deadline4: String,
                        deadline5: String,
                        customer: CustomerReport,
                        agent: AgentReport,
                        items: List[ProductReport],
                        sumPrice: Int)

object OrderReport {
  val fmt = "yyyy. MM. dd."

  def apply(orderId: AssetSupport.IdType,
            orderNumber: String,
            lastModifiedAt: DateTime,
            deadline1: Option[DateTime],
            deadline2: Option[DateTime],
            deadline3: Option[DateTime],
            deadline4: Option[DateTime],
            deadline5: Option[DateTime],
            customer: CustomerReport,
            agent: AgentReport,
            items: List[ProductReport],
            sumPrice: Int): OrderReport = {
    OrderReport(orderId,
      orderNumber,
      lastModifiedAt.toString(fmt),
      deadline1.map(_.toString(fmt)).getOrElse(""),
      deadline2.map(_.toString(fmt)).getOrElse(""),
      deadline3.map(_.toString(fmt)).getOrElse(""),
      deadline4.map(_.toString(fmt)).getOrElse(""),
      deadline5.map(_.toString(fmt)).getOrElse(""),
      customer, agent, items, sumPrice)
  }
}



