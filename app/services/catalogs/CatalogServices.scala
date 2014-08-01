package services.catalogs

import models.{AssetSupport, Catalog}
import play.api.libs.json.Json._
import services._
import play.api.libs.json.Reads




// Fake implmentation of the repository
trait MockCatalogRepositoryComponent extends RepositoryComponent[Catalog] {
  override val repository: Repository = sys.error("TODO") // i.e. mock[CatalogRepository]
}

// Both of these "environments" are just mixins of the component traits.
// Therefore, they have as member variables all services and dependencies.
// All dependencies are compile checked - if a mixin is needed but not provided, the code won't compile.

// "Real" top level environment usable in controllers.
import play.api.Play.current
object production extends MongoRepository[Catalog]("catalogs") with RealServiceComponent[Catalog] with RealRepositoryComponent[Catalog]

// "Fake" top level environment usable in controllers.

