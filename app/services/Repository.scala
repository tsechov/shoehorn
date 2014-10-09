package services

import play.api.libs.json.{JsObject, Json, Writes}
import scala.concurrent.Future
import models.AssetSupport
import models.AssetSupport._
import play.api.libs.json.Json._
import scala.Some
import scala.util.Try
import controllers.utils.LastErrorWrapperImplicits

trait CrudRepositoryComponent {

  trait Repository {


    def getById[A: CollectionName](query: CrudServiceComponent#Query): Future[Option[JsObject]]

    def find[A: CollectionName](query: CrudServiceComponent#Query): Future[List[JsObject]]

    def findAll[A: CollectionName]: Future[List[JsObject]]

    def insert[A](model: A)(implicit w: Writes[A], ev: CollectionName[A]): Future[Try[Unit]]

    def update[A](id: AssetSupport.IdType, model: A)(implicit w: Writes[A], ev: CollectionName[A]): Future[Try[Unit]]

    def remove[A: CollectionName](idType: IdType): Future[Try[Unit]]
  }

  val crudRepository: Repository
}

trait CrudRepository extends CrudRepositoryComponent {
  self: Mongo =>

  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import LastErrorWrapperImplicits._

  override val crudRepository = new Repository {
    override def getById[A: CollectionName](query: CrudServiceComponent#Query) = {

      val result = mongo.find[A](query.query)

      result.map(
        _ match {

          case head :: _ => Some(head)
          case Nil => None

        }
      )

    }

    override def find[A: CollectionName](query: CrudServiceComponent#Query) = mongo.find[A](query.query, query.projection)


    override def findAll[A: CollectionName] = mongo.findAll[A]

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