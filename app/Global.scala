

import play.api._
import play.api.mvc._


/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends WithFilters(CORSFilter()) {


}
