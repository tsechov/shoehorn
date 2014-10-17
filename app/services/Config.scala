package services

import play.api.{Logger, Play}

trait ConfigSupport{
  def configKey(key: String, default: String = "N/A"): String
}
object ConfigSupport extends ConfigSupport{

  import play.api.Play.current

  def configKey(key: String, default: String = "N/A"): String = {
    Play.configuration.getString(key).getOrElse({
      Logger.error(s"missing config key [$key]; using default [$default]")
      default
    })


  }
}