package controllers

import play.api.Play

trait ControllerUtils {
  val contextUrl = Play.application(play.api.Play.current).configuration.getString("context.url").getOrElse("")
}
