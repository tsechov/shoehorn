package controllers

import services.runtime
import play.api.mvc.{Controller, Action}
import play.api.Logger
import play.api.libs.json.Json

object Users extends Controller {

  val userService = runtime userService

  def me = Action {
    request =>
      val userJson = for {
        username <- request.tags.get(filters.AUTHENTICATED_USER)
        user <- userService.getByUserName(username)
      } yield Json.toJson(user)
      userJson.map {
        Ok(_)
      }.getOrElse(NotFound)


  }


}
