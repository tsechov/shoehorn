package models.customer

import models.AssetSupport.IdType
import models._
import models.common._
import org.joda.time.DateTime
import play.api.libs.json.Json


case class TermsOfPayment(
                           term: Int,
                           description: String
                           )

object TermsOfPayment {
  implicit val formats = Json.format[TermsOfPayment]
}

case class MethodOfPayment(
                            method: Int,
                            description: String
                            )

object MethodOfPayment {
  implicit val format = Json.format[MethodOfPayment]
}

case class PaymentSchedule(
                            period: DateTime,
                            percent: String
                            )

object PaymentSchedule {
  implicit val format = Json.format[PaymentSchedule]
}

case class Payment(
                    termsOfPayment: TermsOfPayment,
                    methodOfPayment: MethodOfPayment,
                    paymentSchedule: PaymentSchedule,
                    paymentDay: DateTime,
                    bankAccount: String,
                    bankAccountNumber: String
                    )

object Payment {
  implicit val format = Json.format[Payment]
}


case class CreditInformation(
                              mandatoryCreditLimit: Boolean,
                              creditRating: Int,
                              creditLimit: Int,
                              currency: String
                              )

object CreditInformation {
  implicit val format = Json.format[CreditInformation]
}


case class Discount(
                     multilineDiscount: String,
                     totalDiscount: String,
                     price: Price,
                     lineDiscount: Int
                     )

object Discount {
  implicit val format = Json.format[Discount]
}

case class CustomerIn(
                       _id: IdType,
                       createdAt: DateTime,
                       lastModifiedAt: DateTime,
                       active: Boolean,
                       description: String,

                       name: String,
                       agentId: IdType,
                       taxExemptNumber: Option[String],
                       lineOfBusinessId: IdType,
                       siteId: IdType,
                       companyTypeId: IdType,

                       shopIds: List[IdType],
                       addresses: List[Address],
                       groupIds: List[IdType],
                       contactIds: List[IdType],
                       accountNumber: Option[String]
                       ) extends AssetIn with AssetUpdateBuilder[CustomerUpdate] {
  override def fillup(lastModifiedAt: DateTime) = CustomerUpdate(lastModifiedAt, active, description, name, agentId, taxExemptNumber, lineOfBusinessId, siteId, companyTypeId, shopIds, addresses, groupIds, contactIds, accountNumber)
}

object CustomerIn extends AssetInCompanion[CustomerIn] {
  val format = Json.format[CustomerIn]

  override def collectionName: String = "customers"
}

case class CustomerUpdate(lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,

                          name: String,
                          agentId: IdType,
                          taxExemptNumber: Option[String],
                          lineOfBusinessId: IdType,
                          siteId: IdType,
                          companyTypeId: IdType,

                          shopIds: List[IdType],
                          addresses: List[Address],
                          groupIds: List[IdType],
                          contactIds: List[IdType],
                          accountNumber: Option[String]) extends AssetUpdate

object CustomerUpdate extends AssetUpdateCompanion[CustomerUpdate] {
  val format = Json.format[CustomerUpdate]

  override def collectionName: String = CustomerIn.collectionName
}

case class CustomerCreate(active: Boolean,
                          description: String,

                          name: String,
                          agentId: IdType,
                          taxExemptNumber: Option[String],
                          lineOfBusinessId: IdType,
                          siteId: IdType,
                          companyTypeId: IdType,

                          shopIds: List[IdType],
                          addresses: List[Address],
                          groupIds: List[IdType],
                          contactIds: List[IdType],
                          accountNumber: Option[String]) extends AssetCreate[CustomerIn] {
  override def fillup(b: AssetBase): CustomerIn = CustomerIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name, agentId, taxExemptNumber, lineOfBusinessId, siteId, companyTypeId, shopIds, addresses, groupIds, contactIds, accountNumber)
}

object CustomerCreate {
  implicit val reads = Json.reads[CustomerCreate]
}
