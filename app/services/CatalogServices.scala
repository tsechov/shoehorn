package services


trait CatalogServiceComponent {
  trait CatalogService {
    def getById(id: models.AssetSupport.IdType): Option[models.Catalog]
  }

  val catalogService: CatalogService
}

// Real implmentation
trait RealCatalogServiceComponent extends CatalogServiceComponent {

  // Explicit dependency on User Repository
  self: CatalogRepositoryComponent =>

  override val catalogService: CatalogService = new CatalogService {
    // Use the repository in the service
    override def getById(id: models.AssetSupport.IdType) = catalogRepository.getById(id)
  }
}


trait CatalogRepositoryComponent {
  trait CatalogRepository {
    def getById(id: models.AssetSupport.IdType): Option[models.Catalog]
  }

  val catalogRepository: CatalogRepository
}

// Real implmentation of repository
trait RealCatalogRepositoryComponent extends CatalogRepositoryComponent {
  override val catalogRepository: CatalogRepository = new CatalogRepository {
    override def getById(id: models.AssetSupport.IdType) = sys.error("TODO") // i.e to database, etc.
  }
}

// Fake implmentation of the repository
trait MockCatalogRepositoryComponent extends CatalogRepositoryComponent {
  override val catalogRepository: CatalogRepository = sys.error("TODO") // i.e. mock[CatalogRepository]
}

// Both of these "environments" are just mixins of the component traits.
// Therefore, they have as member variables all services and dependencies.
// All dependencies are compile checked - if a mixin is needed but not provided, the code won't compile.

// "Real" top level environment usable in controllers.
object real extends RealCatalogServiceComponent with RealCatalogRepositoryComponent

// "Fake" top level environment usable in controllers.
object fake extends RealCatalogServiceComponent with MockCatalogRepositoryComponent
