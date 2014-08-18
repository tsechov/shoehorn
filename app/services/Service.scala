package services

import scala.concurrent.Future
import play.api.libs.json._
import models.{AssetUpdate, AssetCreate, AssetSupport}
import play.api.libs.json.Json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.AssetSupport.IdType
import scala.util.{Success, Try}
import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime
import play.api.libs.json.JsObject
import scala.Some
import reactivemongo.core.commands.LastError
import controllers.LastErrorWrapperImplicits


import play.api.Play.current
import play.api.Play


object production extends RealMongo with RealServiceComponent with RealRepositoryComponent


trait ServiceComponent {
  type Query = JsObject

  trait Service {
    def getById[A: CollectionName](id: IdType): Future[Try[Option[JsObject]]]

    def find[A: CollectionName](query: Query): Future[Try[List[JsObject]]]

    def findAll[A: CollectionName]: Future[Try[List[JsObject]]]

    def insert[C <: AssetCreate[A], A](input: C)(implicit w: Writes[A], ev: CollectionName[A]): Future[Try[IdType]]

    def update[A <: AssetUpdate[U], U](id: AssetSupport.IdType)(input: A)(implicit w: Writes[U], ev: CollectionName[U]): Future[Try[Unit]]

    def remove[A: CollectionName](id: AssetSupport.IdType): Future[Try[Unit]]
  }

  val service: Service
}


trait RealServiceComponent extends ServiceComponent {


  self: RepositoryComponent =>

  override val service = new Service {

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

      val model = input.fillup(id, now, now)

      repository.insert[A](model).map(_.map(_ => id))


    }

    override def update[A <: AssetUpdate[U], U](id: AssetSupport.IdType)(input: A)(implicit w: Writes[U], ev: CollectionName[U]): Future[Try[Unit]] = {
      val now = new DateTime()
      val model = input.fillup(now)

      repository.update[U](id, model)
    }

    override def remove[A: CollectionName](id: IdType): Future[Try[Unit]] = {

      repository.remove(id)

    }
  }
}





