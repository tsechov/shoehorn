package controllers

import org.specs2.mutable.Specification
import play.api.mvc.{SimpleResult, Request}
import play.api.http.Writeable

import play.api.test.Helpers._
import play.api.mvc.SimpleResult
import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit


trait CommonControllerSpecs extends Specification {

  val timeout: FiniteDuration = FiniteDuration(5, TimeUnit.SECONDS)

  def request[T](request: Request[T], exptectedStatus: Int)(implicit w: Writeable[T]): SimpleResult = {
    val response = route(request)
    response.isDefined mustEqual true
    val result = Await.result(response.get, timeout)
    result.header.status must equalTo(exptectedStatus)

    result.header.headers must contain(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")

    result
  }
}
