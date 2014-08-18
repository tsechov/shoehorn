package models.common

import models.AssetSupport._
import play.api.libs.json._

case class Price(price: Int, unit: String, quantity: Int)

object Price {
  implicit val format = Json.format[Price]
}

case class Address(
                    typeOfAddressId: IdType,
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
                        typeOfPhoneNumberId: IdType,
                        country: String,
                        extension: Int,
                        number: Int
                        )

object PhoneNumber {
  implicit val format = Json.format[PhoneNumber]
}

case class Email(
                  typeOfEmailId: IdType,
                  address: String
                  )

object Email {
  implicit val format = Json.format[Email]
}

case class Contact(
                    typeOfContactId: IdType,
                    status: String,
                    title: String,
                    firstName: String,
                    lastName: String,
                    phonenumbers: List[PhoneNumber],
                    emails: List[Email]
                    )

object Contact {
  implicit val format = Json.format[Contact]
}

case class District(name: String)

object District {
  implicit val format = Json.format[District]
}
