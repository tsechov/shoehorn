package models

import models.common.ReferenceTo
import models.customer.{ShopIn, AgentIn}
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(username: String, agentId: Option[ReferenceTo[AgentIn]])

object User {


  implicit val format = Json.format[User]


}

case class UserCredential(username: String, password: String)

object UserCredential {
  implicit val reads = Json.reads[UserCredential]
}

