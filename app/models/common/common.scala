package models.common

import models.AssetSupport._
import play.api.libs.json._
import controllers.AssetsBuilder
import models.AssetSupport.RefType

import models.customer.{EmailTypeIn, PhoneNumberTypeIn}

case class Price(price: Int, unit: String, quantity: Int)

object Price {
  implicit val format = Json.format[Price]
}

case class Address(
                    addressTypeId: IdType,
                    country: String,
                    district: String,
                    postalcode: String,
                    city: String,
                    address: String,
                    description: String
                    )

object Address {
  implicit val format = Json.format[Address]
}

case class PhoneNumber(
                        phoneNumberTypeId: RefType[PhoneNumberTypeIn],
                        country: String,
                        extension: String,
                        number: String
                        )

object PhoneNumber {
  implicit val format = Json.format[PhoneNumber]
}

case class Email(
                  emailTypeId: RefType[EmailTypeIn],
                  address: String
                  )

object Email {
  implicit val format = Json.format[Email]
}

trait ContactLike {
  def contactTypeId: IdType

  def status: String

  def title: String

  def firstName: String

  def lastName: String

  def phonenumbers: List[PhoneNumber]

  def emails: List[Email]
}






