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

case class PriceItem(
                      from: Int,
                      to: Int,
                      price: Int
                      )

case class PriceItemIndexed(
                             range: String,
                             price: Int,
                             index: Int
                             )

object PriceItemIndexed {
  def apply(price: PriceItem, index: Int): PriceItemIndexed = PriceItemIndexed(s"${price.from} - ${price.to}", price.price, index)
}

case class ProductReport(
                          modelNumber: String,
                          imageUrl: String,
                          sortiment: List[SortimentItem],
                          prices: List[PriceItemIndexed],
                          totalCount: Int
                          ) {
  if (sortiment.size != ProductReport.SORTIMENT_COUNT) throw new IllegalArgumentException(s"there has to be ${ProductReport.SORTIMENT_COUNT} sortiment items")

}


object ProductReport {

  val MIN_SIZE: Int = 17
  val MAX_SIZE: Int = 40
  val SORTIMENT_COUNT = MAX_SIZE - MIN_SIZE + 1

  def apply(modelNumber: String, imageUrl: String, sortiment: List[SortimentItem], prices: List[PriceItem]): ProductReport = {

    val expandedSortiment = (MIN_SIZE to MAX_SIZE).foldLeft(List[SortimentItem]())((list, pos) => {
      val item = sortiment.find(_.size == pos) match {
        case Some(item) => item
        case None => SortimentItem(pos, 0)
      }
      list :+ item
    })


    ProductReport(modelNumber, imageUrl, expandedSortiment, prices.sortBy(_.from).zipWithIndex.map { case (price, index) => PriceItemIndexed(price, index)}, expandedSortiment.foldLeft(0)((sum, item) => sum + item.count))

  }
}

case class OrderReport(
                        orderId: AssetSupport.IdType,
                        orderNumber: String,
                        lastModifiedAt: String,
                        deadlineName1: String,
                        deadlineDate1: String,
                        deadlineName2: String,
                        deadlineDate2: String,
                        deadlineName3: String,
                        deadlineDate3: String,
                        deadlineName4: String,
                        deadlineDate4: String,
                        deadlineName5: String,
                        deadlineDate5: String,
                        customer: CustomerReport,
                        agent: AgentReport,
                        items: List[ProductReport],
                        sumPrice: Int)

object OrderReport {
  val fmt = "yyyy. MM. dd."

  def apply(orderId: AssetSupport.IdType,
            orderNumber: String,
            lastModifiedAt: DateTime,
            deadline1: Option[(String, DateTime)],
            deadline2: Option[(String, DateTime)],
            deadline3: Option[(String, DateTime)],
            deadline4: Option[(String, DateTime)],
            deadline5: Option[(String, DateTime)],
            customer: CustomerReport,
            agent: AgentReport,
            items: List[ProductReport],
            sumPrice: Int): OrderReport = {
    OrderReport(orderId,
      orderNumber,
      lastModifiedAt.toString(fmt),
      deadline1.map(_._1).getOrElse(""),
      deadline1.map(_._2.toString(fmt)).getOrElse(""),
      deadline2.map(_._1).getOrElse(""),
      deadline2.map(_._2.toString(fmt)).getOrElse(""),
      deadline3.map(_._1).getOrElse(""),
      deadline3.map(_._2.toString(fmt)).getOrElse(""),
      deadline4.map(_._1).getOrElse(""),
      deadline4.map(_._2.toString(fmt)).getOrElse(""),
      deadline5.map(_._1).getOrElse(""),
      deadline5.map(_._2.toString(fmt)).getOrElse(""),
      customer, agent, items, sumPrice)
  }
}



