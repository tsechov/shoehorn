package services

import scala.concurrent.Future
import play.api.libs.json.{JsValue, Reads, JsObject}
import models.AssetSupport
import play.api.libs.json.Json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext


trait ServiceComponent {
  type Query=JsValue
  trait Service {
    def getById[A](id: models.AssetSupport.IdType)(implicit r: Reads[A],ev:CollectionName[A]): Future[Option[A]]
    def find[A](query:Query)(implicit r:Reads[A],ev:CollectionName[A]):Future[List[A]]
    def findAll[A](implicit r:Reads[A],ev:CollectionName[A]):Future[List[A]]
  }

  val service: Service
}

// Real implmentation
trait RealServiceComponent extends ServiceComponent {


  self: RepositoryComponent =>

  override val service = new Service {
    // Use the repository in the service
    override def getById[A](id: models.AssetSupport.IdType)(implicit r: Reads[A],ev:CollectionName[A]) = repository.getById(id)

    override def find[A](query: RealServiceComponent.this.type#Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]] = ???

    override def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]] = ???
  }
}

trait RepositoryComponent {

  trait Repository {
    def getById[A](id: models.AssetSupport.IdType)(implicit r: Reads[A],ev:CollectionName[A]): Future[Option[A]]
  }

  val repository: Repository
}

trait RealRepositoryComponent extends RepositoryComponent {
  self: MongoComponent =>
  override val repository = new Repository {
    override def getById[A](id: AssetSupport.IdType)(implicit r: Reads[A],ev:CollectionName[A]): Future[Option[A]] = {
      val query = obj(AssetSupport.idFieldName -> id)
      mongo.find[A](query).map {
        case head :: _ => Some(head)
        case Nil => None
      }
    }
  }
}

trait MongoComponent {
  val mongo:Mongo
}

trait Mongo {
  def find[A](query: JsObject)(implicit r: Reads[A],ev:CollectionName[A]): Future[List[A]]
}




