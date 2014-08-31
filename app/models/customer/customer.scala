package models.customer

import models.AssetSupport.IdType
import org.joda.time.DateTime
import models.common._
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

case class Customer(
                     id: IdType,
                     createdAt: DateTime,
                     lastModifiedAt: DateTime,
                     active: Boolean,
                     description: String,

                     name: String,
                     agentId: IdType,
                     taxExemptNumber: String,
                     lineOfBusinessId: IdType,
                     siteId: IdType,
                     companyTypeId: IdType,

                     shopIds: List[IdType],
                     addresses: List[Address],
                     groupIds: List[IdType],
                     contactIds: List[IdType]


                     )

object Customer {
  implicit val format = Json.format[Customer]
}
