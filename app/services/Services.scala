package services

import scala.concurrent.Future
import play.api.libs.json.Reads
import models.AssetSupport
import play.api.libs.json.Json._
import play.api.libs.json.JsObject
import play.api.libs.concurrent.Execution.Implicits.defaultContext


trait ServiceComponent[A] {

  trait Service {
    def getById(id: models.AssetSupport.IdType)(implicit r: Reads[A]): Future[Option[A]]
  }

  val service: Service
}

// Real implmentation
trait RealServiceComponent[A] extends ServiceComponent[A] {


  self: RepositoryComponent[A] =>

  override val service = new Service {
    // Use the repository in the service
    override def getById(id: models.AssetSupport.IdType)(implicit r: Reads[A]) = repository.getById(id)
  }
}

trait RepositoryComponent[A] {

  trait Repository {
    def getById(id: models.AssetSupport.IdType)(implicit r: Reads[A]): Future[Option[A]]
  }

  val repository: Repository
}

trait RealRepositoryComponent[A] extends RepositoryComponent[A] {
  self: MongoComponent[A] =>
  override val repository = new Repository {
    override def getById(id: AssetSupport.IdType)(implicit r: Reads[A]): Future[Option[A]] = {
      val query = obj(AssetSupport.idFieldName -> id)
      mongo.find(query).map {
        case head :: _ => Some(head)
        case Nil => None
      }
    }
  }
}

trait MongoComponent[A] {
  val mongo:Mongo[A]
}

trait Mongo[A] {
  def find(query: JsObject)(implicit r: Reads[A]): Future[List[A]]
}




