package controllers

import play.api.Logger
import play.api.mvc.{SimpleResult, RequestHeader, Filter}
import play.api.http.HeaderNames._
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit._

case class CorsFilter() extends Filter {

  import scala.concurrent._
  import ExecutionContext.Implicits.global

  lazy val allowedDomain = play.api.Play.current.configuration.getString("cors.allowed.domain")
  Logger.trace(s"[cors] default allowed domain is $allowedDomain")
  val maxAge = Duration(30, DAYS).toSeconds.toString

  def isPreFlight(r: RequestHeader) = (
    r.method.toLowerCase.equals("options")
      &&
      r.headers.get(ACCESS_CONTROL_REQUEST_METHOD).nonEmpty
    )

  def apply(f: (RequestHeader) => Future[SimpleResult])(request: RequestHeader): Future[SimpleResult] = {
    Logger.trace("[cors] filtering request to add cors")

    if (isPreFlight(request)) {
      Logger.trace("[cors] request is preflight")

      Future.successful(Default.Ok.withHeaders(
        ACCESS_CONTROL_ALLOW_ORIGIN -> allowedDomain.orElse(request.headers.get(ORIGIN)).getOrElse(""),
        ACCESS_CONTROL_ALLOW_METHODS -> request.headers.get(ACCESS_CONTROL_REQUEST_METHOD).getOrElse("*"),
        ACCESS_CONTROL_ALLOW_HEADERS -> (request.headers.get(ACCESS_CONTROL_REQUEST_HEADERS).getOrElse("")),
        ACCESS_CONTROL_MAX_AGE -> maxAge,
        ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"
      ))
    } else {
      Logger.trace("[cors] request is normal")

      f(request).map {
        _.withHeaders(
          ACCESS_CONTROL_ALLOW_ORIGIN -> allowedDomain.orElse(request.headers.get(ORIGIN)).getOrElse(""),
          ACCESS_CONTROL_ALLOW_METHODS -> request.headers.get(ACCESS_CONTROL_REQUEST_METHOD).getOrElse("*"),
          ACCESS_CONTROL_EXPOSE_HEADERS -> (request.headers.get(ACCESS_CONTROL_REQUEST_HEADERS).getOrElse("") + "," + LOCATION + "," + RESOURCE_ID_HEADER),
          ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"
        )
      }
    }
  }
}