package controllers

import play.api._
import play.api.mvc._

class CakeTest {


  // Domain object
  case class User(id: String, name: String)

  // Trait defining the service component
  trait UserServiceComponent {
    trait UserService {
      def getUserName(id: String): String
    }

    val userService: UserService
  }

  // Real implmentation
  trait RealUserServiceComponent extends UserServiceComponent {

    // Explicit dependency on User Repository
    self: UserRepositoryComponent =>

    override val userService: UserService = new UserService {
      // Use the repository in the service
      override def getUserName(id: String) = userRepository.getUser(id).name
    }
  }

  // Trait defining the repository
  trait UserRepositoryComponent {
    trait UserRepository {
      def getUser(id: String): User
    }

    val userRepository: UserRepository
  }

  // Real implmentation of repository
  trait RealUserRepositoryComponent extends UserRepositoryComponent {
    override val userRepository: UserRepository = new UserRepository {
      override def getUser(id: String) = error("todo") // i.e to database, etc.
    }
  }

  // Fake implmentation of the repository
  trait MockUserRepositoryComponent extends UserRepositoryComponent {
    override val userRepository: UserRepository = sys.error("todo") // i.e. mock[UserRepository]
  }


  // Both of these "environments" are just mixins of the component traits.
  // Therefore, they have as member variables all services and dependencies.
  // All dependencies are compile checked - if a mixin is needed but not provided, the code won't compile.

  // "Real" top level environment usable in controllers.
  object real extends RealUserServiceComponent with RealUserRepositoryComponent

  // "Fake" top level environment usable in controllers.
  object fake extends RealUserServiceComponent with MockUserRepositoryComponent

  // Use the real environment in the top level controller
  object Application extends Controller {

    val userService = real userService

    def name(id: String) = Action {
      Ok(userService.getUserName(id))
    }

  }

  // Use the fake environment in the test.
  class SomeTest {

    val userService = fake userService

    def sometTest {
      // set up the mock ...
      val name = userService.getUserName("id1")
      // do assertion
    }
  }

  // Or you can define the components add hoc.
  class SomeOtherTest extends RealUserServiceComponent with UserRepositoryComponent {
    override val userRepository = error("todo") // mock or something

    def someOtherTest {
      // set up the mock
      val name = userService.getUserName("id2")
      // do assertion
    }
  }
}
