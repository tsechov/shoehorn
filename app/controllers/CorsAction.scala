package controllers

import play.api.mvc.{SimpleResult, Results, Request, ActionBuilder}
import scala.concurrent.Future
import play.api.http.HeaderNames._
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit._


object CorsAction extends ActionBuilder[Request] with Results {
  val maxAge = Duration(30,DAYS).toSeconds.toString

  val allowHeaders = List("Content-Type",
    "X-Auth-Token", "X-HTTP-Method-Override", "X-Json", "X-Prototype-Version", "X-Requested-With").mkString(", ")

  val allowMethods = List("POST", "GET", "PUT", "DELETE", "OPTIONS").mkString(", ")

  val allowCredentials = true.toString

  def corsPreflight[A](origin: String) =
    Ok.withHeaders(

      ACCESS_CONTROL_ALLOW_METHODS -> allowMethods,
      ACCESS_CONTROL_ALLOW_HEADERS -> allowHeaders,
      ACCESS_CONTROL_ALLOW_CREDENTIALS -> allowCredentials,
      ACCESS_CONTROL_MAX_AGE -> maxAge,
      ACCESS_CONTROL_ALLOW_ORIGIN -> origin
    )

  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]) = {
    implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
    val origin = request.headers.get(ORIGIN).getOrElse("*")

    if (request.method == "OPTIONS") {
      Future.successful(corsPreflight(origin))
    } else {
      block(request).map(res =>
        res.withHeaders(
          ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
          ACCESS_CONTROL_ALLOW_CREDENTIALS -> allowCredentials,
          ACCESS_CONTROL_EXPOSE_HEADERS -> LOCATION))
    }
  }
}