package controllers

import javax.inject.{Singleton, Inject}
import services.UUIDGenerator
import org.slf4j.{LoggerFactory, Logger}
import play.api.mvc._
import play.Play

/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the UUID generator service.
 * @param uuidGenerator the UUID generator service we wish to receive.
 */
@Singleton
class Application @Inject()(uuidGenerator: UUIDGenerator) extends Controller {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Application])

  def index = Action {
    val gitCommitId = Play.application.configuration.getString("git.commit")
    val scmUrl = Play.application.configuration.getString("scm.url")

    Ok(views.html.index(scmUrl + "/commit/" + gitCommitId))
  }

  def randomUUID = Action {
    Ok(uuidGenerator.generate.toString)
  }


  def preflight(path: String) = Action {
    Ok
  }

  def index2 = Action {
    val gitCommitId = Play.application.configuration.getString("git.commit")
    val scmUrl = Play.application.configuration.getString("scm.url")

    Ok(views.html.index2(scmUrl + "/commit/" + gitCommitId))
  }
}
