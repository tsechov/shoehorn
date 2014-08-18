package models.customer

import models.AssetSupport
import org.joda.time.DateTime


case class PhoneNumber(
                        `type`: String,
                        country: String,
                        extension: Int,
                        number: Int)

)

case class Email(
                  `type`: String,
                  address: String
                  )

case class Customer(
                     id: AssetSupport.IdType,
                     createdAt: DateTime,
                     lastModifiedAt: DateTime,
                     active: Boolean,
                     description: String,
                     name: String,
                     agent: Agent,
                     active: Boolean,
                     places: List[Place],
                     `type`: String,
                     taxExemptNumber: Stirng,
                     shops: List[Shop],
                     addresses: List[Address],
                     groups: List[Group],
                     contacts: List[Contact],
                     lineOfBusiness: String,
                     creditInformation: CreditInformation,
                     site: District,
                     discount: Discount,
                     payment: Payment)


case class Contact(
                    `type`: String,
                    status: String,
                    title: String,
                    firstName: String,
                    lastName: String,
                    phonenumbers: List[PhoneNumber],
                    emails: List[Email]

                    )


case class Agent(

                  ) extends Contact
