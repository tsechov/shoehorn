package services

import play.api.libs.json.{Json, Writes, Reads}
import scala.concurrent.Future
import reactivemongo.core.commands.LastError
import models.AssetSupport
import models.AssetSupport._
import play.api.libs.json.Json._
import scala.Some
import scala.util.{Success, Try}
import controllers.LastErrorWrapperImplicits

trait RepositoryComponent {

  trait Repository {


    def getById[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[Try[Option[A]]]

    def find[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]): Future[Try[List[A]]]

    def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[Try[List[A]]]

    def insert[A](model: A)(implicit w: Writes[A], ev: CollectionName[A]): Future[Try[Unit]]

    def update[A](id: AssetSupport.IdType, model: A)(implicit w: Writes[A], ev: CollectionName[A]): Future[Try[Unit]]

    def remove[A: CollectionName](idType: IdType): Future[Try[Unit]]
  }

  val repository: Repository
}

trait RealRepositoryComponent extends RepositoryComponent {
  self: Mongo =>

  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import LastErrorWrapperImplicits._

  override val repository = new Repository {
    override def getById[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]) = {

      val result = mongo.find[A](query)

      result.map(_.map {
        _ match {

          case head :: _ => Some(head)
          case Nil => None

        }
      })

    }

    override def find[A](query: ServiceComponent#Query)(implicit r: Reads[A], ev: CollectionName[A]) = mongo.find[A](query)

    override def findAll[A](implicit r: Reads[A], ev: CollectionName[A]) = mongo.findAll[A]

    override def insert[A](model: A)(implicit w: Writes[A], ev: CollectionName[A]) = {
      val jsonToInsert = Json.toJson[A](model)
      mongo.insert[A](jsonToInsert).map(_.orFail.map(_ => Unit))

    }

    override def update[A](id: IdType, model: A)(implicit w: Writes[A], ev: CollectionName[A]) = {
      val jsonToUpdate = Json.toJson[A](model)
      val selector = obj(AssetSupport.idFieldName -> id)
      mongo.update[A](selector, jsonToUpdate).map(_.orFail.map(_ => Unit))
    }

    override def remove[A: CollectionName](id: IdType) = {
      val selector = obj(AssetSupport.idFieldName -> id)
      mongo.remove[A](selector).map(_.orFail.map(_ => Unit))
    }
  }
}