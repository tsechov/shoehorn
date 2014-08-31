package controllers

import play.api.mvc._
import play.Play


object Application extends Controller {


  def index = Action {
    val gitCommitId = Play.application.configuration.getString("git.commit")
    val scmUrl = Play.application.configuration.getString("scm.url")

    val version = release.CurrentVersion()

    if (version.endsWith("SNAPSHOT")) {
      Ok(views.html.index(scmUrl + "/commit/" + gitCommitId, version))
    } else {
      Ok(views.html.index(scmUrl + "/tree/v" + version, version))
    }

  }

  def version = Action {
    Ok(release.CurrentVersion())
  }


  def preflight(path: String) = Action {
    Ok
  }


}
