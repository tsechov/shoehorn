package models.customer

import models.common._
import play.api.libs.json.{Reads, Json}
import models._
import models.AssetSupport.IdType
import org.joda.time.DateTime
import services.CollectionName

case class AgentIn(
                    _id: IdType,
                    createdAt: DateTime,
                    lastModifiedAt: DateTime,
                    active: Boolean,
                    description: String,
                    typeOfContactId: IdType,
                    status: String,
                    title: String,
                    firstName: String,
                    lastName: String,
                    phonenumbers: List[PhoneNumber],
                    emails: List[Email]
                    ) extends ContactLike with AssetIn with AssetUpdateBuilder[AgentUpdate] {
  override def fillup(lastModifiedAt: DateTime) =
    AgentUpdate(
      lastModifiedAt,
      active,
      description,
      typeOfContactId,
      status,
      title,
      firstName,
      lastName,
      phonenumbers,
      emails)
}

object AgentIn extends DateFormatSupport {
  implicit val format = Json.format[AgentIn]
  implicit val collectionName = new CollectionName[AgentIn] {
    override def get: String = "agents"
  }
}

case class AgentUpdate(lastModifiedAt: DateTime,
                       active: Boolean,
                       description: String,
                       typeOfContactId: IdType,
                       status: String,
                       title: String,
                       firstName: String,
                       lastName: String,
                       phonenumbers: List[PhoneNumber],
                       emails: List[Email]) extends ContactLike with AssetUpdate

object AgentUpdate extends DateFormatSupport {


  implicit val format = Json.format[AgentUpdate]
  implicit val collectionName = new CollectionName[AgentUpdate] {
    override def get: String = AgentIn.collectionName.get
  }


}

case class AgentCreate(active: Boolean,
                       description: String,
                       typeOfContactId: IdType,
                       status: String,
                       title: String,
                       firstName: String,
                       lastName: String,
                       phonenumbers: List[PhoneNumber],
                       emails: List[Email]) extends ContactLike with AssetCreate[AgentIn] {
  self =>
  override def fillup(id: IdType, createdAt: DateTime, lastModifiedAt: DateTime) = {
    AgentIn(
      id,
      createdAt,
      lastModifiedAt,
      active,
      description,
      typeOfContactId,
      status,
      title,
      firstName,
      lastName,
      phonenumbers,
      emails)
  }

}

object AgentCreate {
  implicit val reads = Json.reads[AgentCreate]
}