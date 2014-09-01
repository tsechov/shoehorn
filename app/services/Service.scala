package services

import scala.concurrent.Future
import play.api.libs.json._
import models.{AssetBase, AssetUpdateBuilder, AssetCreate, AssetSupport}
import play.api.libs.json.Json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.AssetSupport.IdType
import scala.util.{Success, Try}
import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime
import play.api.libs.json.JsObject


import play.api.Play.current


object production
  extends RealMongo with RealServiceComponent with RealRepositoryComponent
  with EnvVarUserRepository with UserService
  with MongoOrderRepository with OrderService


trait ServiceComponent {
  type Query = JsObject

  trait Service {
    def getById[A: CollectionName](id: IdType): Future[Try[Option[JsObject]]]

    def find[A: CollectionName](query: Query): Future[Try[List[JsObject]]]

    def findAll[A: CollectionName]: Future[Try[List[JsObject]]]

    def insert[C <: AssetCreate[A], A](input: C)(implicit w: Writes[A], ev: CollectionName[A]): Future[Try[IdType]]

    def update[A <: AssetUpdateBuilder[U], U](id: AssetSupport.IdType)(input: A)(implicit w: Writes[U], ev: CollectionName[U]): Future[Try[Unit]]

    def remove[A: CollectionName](id: AssetSupport.IdType): Future[Try[Unit]]
  }

  val service: Service
}


trait RealServiceComponent extends ServiceComponent {


  self: RepositoryComponent =>


  override val service = new Service {
    val updateCommand = obj(AssetSupport.activeFieldName -> false)

    override def getById[A: CollectionName](id: models.AssetSupport.IdType) = {
      val query = obj(AssetSupport.idFieldName -> id)
      repository.getById[A](query) map {
        Success(_)
      }
    }

    override def find[A: CollectionName](query: ServiceComponent#Query) = repository.find[A](query).map(Success(_))

    override def findAll[A: CollectionName] = repository.findAll[A].map(Success(_))

    override def insert[C <: AssetCreate[A], A](input: C)(implicit w: Writes[A], ev: CollectionName[A]) = {
      val id = BSONObjectID.generate.stringify
      val now = new DateTime()

      val model: A = input.fillup(AssetBase(id, now, now))

      repository.insert[A](model).map(_.map(_ => id))


    }

    override def update[A <: AssetUpdateBuilder[U], U](id: AssetSupport.IdType)(input: A)(implicit w: Writes[U], ev: CollectionName[U]): Future[Try[Unit]] = {
      val now = new DateTime()
      val model = input.fillup(now)

      repository.update[U](id, model)
    }

    override def remove[A: CollectionName](id: IdType): Future[Try[Unit]] = {

      val collectionName = implicitly[CollectionName[A]].get
      implicit val cn = new CollectionName[JsObject] {
        override def get: String = collectionName
      }
      repository.update[JsObject](id, updateCommand)

    }
  }
}





