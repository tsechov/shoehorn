package models.customer

import models.common._
import play.api.libs.json.Json
import models._
import models.AssetSupport.IdType
import org.joda.time.DateTime
import services.CollectionName

case class ContactIn(
                      _id: IdType,
                      createdAt: DateTime,
                      lastModifiedAt: DateTime,
                      active: Boolean,
                      description: String,
                      contactTypeId: IdType,
                      status: String,
                      title: String,
                      firstName: String,
                      lastName: String,
                      phonenumbers: List[PhoneNumber],
                      emails: List[Email]
                      ) extends ContactLike with AssetIn with AssetUpdateBuilder[ContactUpdate] {
  override def fillup(lastModifiedAt: DateTime) =
    ContactUpdate(
      lastModifiedAt,
      active,
      description,
      contactTypeId,
      status,
      title,
      firstName,
      lastName,
      phonenumbers,
      emails)
}

object ContactIn extends DateFormatSupport {
  implicit val format = Json.format[ContactIn]
  implicit val collectionName = new CollectionName[ContactIn] {
    override def get: String = "contacts"
  }
}

case class ContactUpdate(lastModifiedAt: DateTime,
                         active: Boolean,
                         description: String,
                         contactTypeId: IdType,
                         status: String,
                         title: String,
                         firstName: String,
                         lastName: String,
                         phonenumbers: List[PhoneNumber],
                         emails: List[Email]) extends ContactLike with AssetUpdate

object ContactUpdate extends DateFormatSupport {


  implicit val format = Json.format[ContactUpdate]
  implicit val collectionName = new CollectionName[ContactUpdate] {
    override def get: String = ContactIn.collectionName.get
  }


}

case class ContactCreate(active: Boolean,
                         description: String,
                         contactTypeId: IdType,
                         status: String,
                         title: String,
                         firstName: String,
                         lastName: String,
                         phonenumbers: List[PhoneNumber],
                         emails: List[Email]) extends ContactLike with AssetCreate[ContactIn] {
  self =>
  override def fillup(b: AssetBase) = {
    ContactIn(
      b.id,
      b.createdAt,
      b.lastModifiedAt,
      active,
      description,
      contactTypeId,
      status,
      title,
      firstName,
      lastName,
      phonenumbers,
      emails)
  }

}

object ContactCreate {
  implicit val reads = Json.reads[ContactCreate]
}