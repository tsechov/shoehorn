

import filters.{BasicAuthFilter, VersionFilter, CorsFilter}
import play.api.Logger
import play.api.mvc._


object Global extends WithFilters(CorsFilter, VersionFilter, BasicAuthFilter) {
  override def onError(request: RequestHeader, ex: Throwable) = {
    Logger.error("unhandled error", ex)
    super.onError(request, ex)

  }

}
