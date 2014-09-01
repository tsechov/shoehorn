package controllers

import services.production
import play.api.mvc.{Controller, Action}

object Users extends Controller {

  val userService = production userService

  def me = Action {
    Ok
  }


}
