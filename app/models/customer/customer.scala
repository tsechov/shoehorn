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


case class Place(
                  year: String,
                  season: String,
                  place: Int
                  )

object Place {
  implicit val format = Json.format[Place]
}


case class Group(
                  name: String
                  )

object Group {
  implicit val format = Json.format[Group]
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

                     companyTypeId: IdType,
                     taxExemptNumber: String,
                     shops: List[ShopIn],
                     addresses: List[Address],
                     groupIds: List[IdType],
                     contactIds: List[IdType],
                     lineOfBusiness: String,
                     creditInformation: Option[CreditInformation],
                     siteId: IdType)

object Customer {
  implicit val format = Json.format[Customer]
}
