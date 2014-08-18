package models.customer

import models.AssetSupport.{IdType, UrlType}
import org.joda.time.DateTime
import models.product.Price


case class Customer(
                     id: IdType,
                     createdAt: DateTime,
                     lastModifiedAt: DateTime,
                     active: Boolean,
                     description: String,

                     name: String,
                     agent: Agent,
                     places: List[Place],
                     typeOfCompanyId: IdType,
                     taxExemptNumber: String,
                     shops: List[Shop],
                     addresses: List[Address],
                     customergroups: List[Group],
                     contacts: List[Contact],
                     lineOfBusiness: String,
                     creditInformation: CreditInformation,
                     site: District,
                     discount: Discount,
                     payment: Payment)

case class PhoneNumber(
                        typeOfPhoneNumberId: IdType,
                        country: String,
                        extension: Int,
                        number: Int

                        )

case class Email(
                  typeOfEmailId: IdType,
                  address: String
                  )


case class Contact(
                    typeOfContactId: IdType,
                    status: String,
                    title: String,
                    firstName: String,
                    lastName: String,
                    phonenumbers: List[PhoneNumber],
                    emails: List[Email]
                    )


case class Agent(
                  warehouse: Warehouse,
                  districts: List[District],
                  contact: Contact)

case class Warehouse(
                      name: String,
                      address: Address,
                      status: Boolean,
                      url: UrlType
                      )

case class District(name: String)

case class Address(
                    typeOfAddressId: IdType,
                    country: String,
                    district: String,
                    postalcode: String,
                    city: String,
                    address: String,
                    description: String
                    )

case class Payment(
                    termsOfPayment: TermsOfPayment,
                    methodOfPayment: MethodOfPayment,
                    paymentSchedule: PaymentSchedule,
                    paymentDay: DateTime,
                    bankAccount: String,
                    bankAccountNumber: String
                    )

case class TermsOfPayment(
                           term: Int,
                           description: String
                           )

case class MethodOfPayment(
                            method: Int,
                            description: String
                            )

case class PaymentSchedule(
                            period: DateTime,
                            percent: String
                            )

case class Place(
                  year: String,
                  season: String,
                  place: Int
                  )

case class Shop(
                 name: String,
                 shopAddress: Address,
                 invoiceAddress: Address,
                 shipmentAddress: Address,
                 status: Boolean
                 )

case class Group(
                  name: String
                  )

case class CreditInformation(
                              mandatoryCreditLimit: Boolean,
                              creditRating: Int,
                              creditLimit: Int,
                              currency: String
                              )

case class Discount(
                     multilineDiscount: String,
                     totalDiscount: String,
                     price: Price,
                     lineDiscount: Int
                     )