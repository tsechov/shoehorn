

import filters.{BasicAuthFilter, VersionFilter, CorsFilter}
import play.api.mvc._



object Global extends WithFilters(CorsFilter, VersionFilter, BasicAuthFilter) {


}
