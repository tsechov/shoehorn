package models.order

import models.AssetBase
import models.AssetSupport._
import models._
import models.common.Address
import models.product._
import org.joda.time.DateTime
import play.api.libs.json.Json


case class OrderItem(productId: Option[IdType], product: ProductIn, quantity: Int, size: Int)

object OrderItem {
  implicit val format = Json.format[OrderItem]
}

case class Deadline(deadlineTypeId: IdType, date: DateTime)

object Deadline extends DateFormatSupport {
  implicit val format = Json.format[Deadline]
}

case class OrderIn(_id: IdType,
                   createdAt: DateTime,
                   lastModifiedAt: DateTime,
                   active: Boolean,
                   description: String,
                   orderNumber: String,
                   orderId: Int,
                   originatorId: IdType,
                   customerId: IdType,
                   billingAddress: Address,
                   shippingAddress: Address,
                   deadlines: List[Deadline],
                   total: Int,
                   numberOfPairs: Int,
                   items: List[OrderItem],
                   agentNotifiedAt: Option[DateTime],
                   comment: Option[String],
                   customerNotifiedAt: Option[DateTime]) extends AssetIn with AssetUpdateBuilder[OrderUpdate] {
  override def fillup(lastModifiedAt: DateTime): OrderUpdate = OrderUpdate(lastModifiedAt, active, description, orderNumber, orderId, originatorId, customerId, billingAddress, shippingAddress, deadlines, total, numberOfPairs, items, agentNotifiedAt, comment, customerNotifiedAt)
}

object OrderIn extends AssetInCompanion[OrderIn] {
  val collectionName = "orders"


  val format = Json.format[OrderIn]
}


case class OrderUpdate(lastModifiedAt: DateTime,
                       active: Boolean,
                       description: String,
                       orderNumber: String,
                       orderId: Int,
                       originatorId: IdType,
                       customerId: IdType,
                       billingAddress: Address,
                       shippingAddress: Address,
                       deadlines: List[Deadline],
                       total: Int,
                       numberOfPairs: Int,
                       items: List[OrderItem],
                       agentNotifiedAt: Option[DateTime],
                       comment: Option[String],
                       customerNotifiedAt: Option[DateTime]) extends AssetUpdate

object OrderUpdate extends AssetUpdateCompanion[OrderUpdate] {
  val format = Json.format[OrderUpdate]
  val collectionName = OrderIn.collectionName
}

case class OrderCreate(active: Boolean,
                       description: String,
                       orderNumber: String,
                       orderId: Int,
                       originatorId: IdType,
                       customerId: IdType,
                       billingAddress: Address,
                       shippingAddress: Address,
                       deadlines: List[Deadline],
                       total: Int,
                       numberOfPairs: Int,
                       items: List[OrderItem],
                       agentNotifiedAt: Option[DateTime],
                       comment: Option[String],
                       customerNotifiedAt: Option[DateTime]) extends AssetCreate[OrderIn] {
  override def fillup(b: AssetBase) = OrderIn(b.id, b.createdAt, b.lastModifiedAt, active, description, orderNumber, orderId, originatorId, customerId, billingAddress, shippingAddress, deadlines, total, numberOfPairs, items, agentNotifiedAt, comment, customerNotifiedAt)

}

object OrderCreate {
  implicit val reads = Json.reads[OrderCreate]
}


