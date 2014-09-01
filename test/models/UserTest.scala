package models

import org.specs2.mutable.Specification
import models.common.ReferenceTo
import models.customer.AgentIn
import play.api.libs.json.Json

class UserTest extends Specification {
  "User" should {

    "should be transformable to and from json" in {

      val user = User("username", Some(ReferenceTo("123")))
      val jsonString = Json.prettyPrint(Json.toJson(user))
      println(jsonString)
      val parsed = Json.parse(jsonString).validate[User].get
      success
    }


  }
}
