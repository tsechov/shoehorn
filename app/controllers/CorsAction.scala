package controllers

import play.api.mvc.{SimpleResult, Results, Request, ActionBuilder}
import scala.concurrent.Future
import play.api.http.HeaderNames._


object CorsAction extends ActionBuilder[Request] with Results{
//  val MaxAge = 60 * 60 * 24 * 30
val MaxAge = 1
  val AllowHeaders = List(       "Content-Type",
    "X-Auth-Token", "X-HTTP-Method-Override", "X-Json", "X-Prototype-Version", "X-Requested-With")

  val AllowMethods = List("POST", "GET", "PUT", "DELETE", "OPTIONS")

  val AllowCredentials = true

  def cors[A](origin: String) =
    Ok.withHeaders(

      ACCESS_CONTROL_ALLOW_METHODS -> AllowMethods.mkString(", "),
      ACCESS_CONTROL_ALLOW_HEADERS -> AllowHeaders.mkString(", "),
      ACCESS_CONTROL_ALLOW_CREDENTIALS -> AllowCredentials.toString,
      ACCESS_CONTROL_MAX_AGE -> MaxAge.toString,
      ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
      "Access-Control-Expose-Headers" -> "Location")

  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]) = {
    implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
    val origin = request.headers.get(ORIGIN).getOrElse("*")

    if (request.method == "OPTIONS") {
      Future.successful(cors(origin))
    } else {
      block(request).map(res =>
        res.withHeaders(
          ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
          ACCESS_CONTROL_ALLOW_CREDENTIALS -> AllowCredentials.toString,
          "Access-Control-Expose-Headers" -> "Location"
    }
  }
}
