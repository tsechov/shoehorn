package filters

import play.api.mvc.{RequestHeader, Filter}
import scala.concurrent.Future

import play.api.mvc.SimpleResult
import play.api.libs.concurrent.Execution.Implicits.defaultContext


case class VersionFilter() extends Filter {
  override def apply(f: (RequestHeader) => Future[SimpleResult])(rh: RequestHeader) = {
    f(rh).map(_.withHeaders(VERSION_HEADER -> release.CurrentVersion()))
  }
}
