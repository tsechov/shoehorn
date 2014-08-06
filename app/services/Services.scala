package services

import scala.concurrent.Future
import play.api.libs.json._
import models.{AssetUpdate, AssetCreate, AssetSupport}
import play.api.libs.json.Json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.AssetSupport.IdType
import scala.util.Try
import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime
import play.api.libs.json.JsObject
import scala.Some
import reactivemongo.core.commands.LastError
import controllers.LastErrorWrapperImplicits


trait ServiceComponent {
  type Query = JsObject

  trait Service {
    def getById[A](id: IdType)(implicit r: Reads[A], ev: CollectionName[A]): Future[Option[A]]

    def find[A](query: Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]

    def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]

    def insert[C <: AssetCreate[A], A](input: C)(implicit w: Writes[A], ev: CollectionName[A]): Future[Try[IdType]]

    def update[A <: AssetUpdate[U], U](id: AssetSupport.IdType)(input: A)(implicit w: Writes[U], ev: CollectionName[U]): Future[Try[Unit]]

    def remove[A: CollectionName](id: AssetSupport.IdType): Future[Try[Unit]]
  }

  val service: Service
}


trait RealServiceComponent extends ServiceComponent {


  self: RepositoryComponent =>

  override val service = new Service {

    override def getById[A](id: models.AssetSupport.IdType)(implicit r: Reads[A], ev: CollectionName[A]) = {
      val query = obj(AssetSupport.idFieldName -> id)
      repository.getById[A](query)
    }

    override def find[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]) = repository.find[A](query)

    override def findAll[A](implicit r: Reads[A], ev: CollectionName[A]) = repository.findAll[A]

    override def insert[C <: AssetCreate[A], A](input: C)(implicit w: Writes[A], ev: CollectionName[A]) = {
      val id = BSONObjectID.generate.stringify
      val now = new DateTime()

      val model = input.fillup(id, now, now)


      import LastErrorWrapperImplicits._

      repository.insert[A](model).map(_.orFail.map(_ => id))


    }

    override def update[A <: AssetUpdate[U], U](id: AssetSupport.IdType)(input: A)(implicit w: Writes[U], ev: CollectionName[U]): Future[Try[Unit]] = {
      val now = new DateTime()
      val model = input.fillup(now)
      import LastErrorWrapperImplicits._
      repository.update[U](id, model).map(_.orFail.map(_ => Unit))
    }

    override def remove[A: CollectionName](id: IdType): Future[Try[Unit]] = {
      import LastErrorWrapperImplicits._
      repository.remove(id).map(_.orFail.map(_ => Unit))

    }
  }
}

trait RepositoryComponent {

  trait Repository {


    def getById[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[Option[A]]

    def find[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]

    def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]

    def insert[A](model: A)(implicit w: Writes[A], ev: CollectionName[A]): Future[LastError]

    def update[A](id: AssetSupport.IdType, model: A)(implicit w: Writes[A], ev: CollectionName[A]): Future[LastError]

    def remove[A: CollectionName](idType: IdType): Future[LastError]
  }

  val repository: Repository
}

trait RealRepositoryComponent extends RepositoryComponent {
  self: MongoComponent =>
  override val repository = new Repository {
    override def getById[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[Option[A]] = {

      val result = mongo.find[A](query)

      result.map {
        case head :: _ => Some(head)
        case Nil => None
      }
    }

    override def find[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]) = mongo.find[A](query)

    override def findAll[A](implicit r: Reads[A], ev: CollectionName[A]) = mongo.findAll[A]

    override def insert[A](model: A)(implicit w: Writes[A], ev: CollectionName[A]) = {
      val jsonToInsert = Json.toJson[A](model)
      mongo.insert[A](jsonToInsert)

    }

    override def update[A](id: IdType, model: A)(implicit w: Writes[A], ev: CollectionName[A]): Future[LastError] = {
      val jsonToUpdate = Json.toJson[A](model)
      val selector = obj(AssetSupport.idFieldName -> id)
      mongo.update[A](selector, jsonToUpdate)
    }

    override def remove[A: CollectionName](id: IdType): Future[LastError] = {
      val selector = obj(AssetSupport.idFieldName -> id)
      mongo.remove[A](selector)
    }
  }
}

trait MongoComponent {
  val mongo: Mongo
}

trait Mongo {
  def find[A](query: JsObject)(implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]

  def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]

  def insert[A: CollectionName](jsonToInsert: JsValue): Future[LastError]

  def update[A: CollectionName](selector: JsObject, json: JsValue): Future[LastError]

  def remove[A: CollectionName](selector: JsObject): Future[LastError]
}




