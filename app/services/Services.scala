package services

import scala.concurrent.Future
import play.api.libs.json.{JsValue, Reads, JsObject}
import models.AssetSupport
import play.api.libs.json.Json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext


trait ServiceComponent {
  type Query=JsObject
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

    override def getById[A](id: models.AssetSupport.IdType)(implicit r: Reads[A],ev:CollectionName[A]) = repository.getById[A](id)

    override def find[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]] = repository.find[A](query)

    override def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]] = repository.findAll[A]
  }
}

trait RepositoryComponent {

  trait Repository {
    def getById[A](id: models.AssetSupport.IdType)(implicit r: Reads[A],ev:CollectionName[A]): Future[Option[A]]
    def find[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]
    def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]
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

    override def find[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]] = mongo.find[A](query)
    override def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]] = mongo.findAll[A]
  }
}

trait MongoComponent {
  val mongo:Mongo
}

trait Mongo {
  def find[A](query: JsObject)(implicit r: Reads[A],ev:CollectionName[A]): Future[List[A]]
  def findAll[A](implicit r: Reads[A],ev:CollectionName[A]): Future[List[A]]
}




