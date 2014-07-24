import com.google.inject.{Guice, AbstractModule}
import play.api.GlobalSettings
import play.api.mvc.{SimpleResult, Action}
import services.{SimpleUUIDGenerator, UUIDGenerator}
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.http.HeaderNames._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends GlobalSettings {

  /**
   * Bind types such that whenever UUIDGenerator is required, an instance of SimpleUUIDGenerator will be used.
   */
  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
      bind(classOf[UUIDGenerator]).to(classOf[SimpleUUIDGenerator])
    }
  })

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)


//  override def doFilter(action: EssentialAction): EssentialAction = EssentialAction { request =>
//    action.apply(request).map(
//      _.withHeaders(
//        (ACCESS_CONTROL_ALLOW_ORIGIN, "*"),
//        (ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT"),
//        (ACCESS_CONTROL_ALLOW_HEADERS, CONTENT_TYPE)
//      )
//    )
//  }

}
