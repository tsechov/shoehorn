package services

import models.{UserCredential, User}
import play.api.libs.json.{Reads, JsError, JsSuccess, Json}
import play.api.Logger
import scala.util.{Failure, Success, Try}

trait UserServiceComponent {

  trait Service {
    def getByUserName(username: String): Option[User]

    def authenticate(username: String, password: String): Boolean
  }

  val userService: Service

}

trait UserService extends UserServiceComponent {
  this: UserRepositoryComponent =>
  override val userService = new Service {
    override def getByUserName(username: String) = userRepository.getByUserName(username)

    override def authenticate(username: String, password: String) = userRepository.authenticate(username, password)
  }
}

trait UserRepositoryComponent {

  trait Repository {
    def getByUserName(username: String): Option[User]

    def authenticate(username: String, password: String): Boolean
  }

  val userRepository: Repository

}


trait EnvVarUserRepository extends UserRepositoryComponent {
  override val userRepository = new Repository {
    override def getByUserName(username: String) = find[User](_.username == username)

    override def authenticate(username: String, password: String) = find[UserCredential]((cr) => cr.username == username && cr.password == password).nonEmpty


    private def find[A](filter: (A) => Boolean)(implicit r: Reads[List[A]]): Option[A] = {
      val usersString = sys.env.get("SHOEHORN_USERS").getOrElse("[]")

      Try(Json.parse(usersString)).map {
        _.validate[List[A]] match {
          case JsSuccess(users, _) => users.find(filter)
          case JsError(error) => {
            Logger.error(s"cannot parse users from envvar['SHOEHORN_USERS']: $error")
            None
          }
        }
      } match {
        case Success(result) => result
        case Failure(error) => {
          Logger.error(s"cannot read users from envvar['SHOEHORN_USERS']: $error")
          None
        }
      }
    }
  }
}
