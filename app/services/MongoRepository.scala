package services

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Application
import scala.concurrent.Future
import play.api.libs.json.{JsValue, Json, Reads, JsObject}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.core.commands.LastError
import play.api.libs.json.Json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.JsObject
import models.AssetSupport


class MongoRepository(implicit app: Application) extends MongoComponent{
  /** Returns the current instance of the driver. */
  def driver = ReactiveMongoPlugin.driver
  /** Returns the current MongoConnection instance (the connection pool manager). */
  def connection = ReactiveMongoPlugin.connection
  /** Returns the default database (as specified in `application.conf`). */
  def db = ReactiveMongoPlugin.db

  private val emptyQuery=Json.obj()

  def collection(name:String): JSONCollection = db.collection[JSONCollection](name)

  override val mongo = new Mongo {
    def find[A](query:JsObject)(implicit r:Reads[A],ev:CollectionName[A]):Future[List[A]] = {
      collection(ev.get).find(query).cursor[A].collect[List]()
    }

    override def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]] = collection(ev.get).find(emptyQuery).cursor[A].collect[List]()

    override def insert[A: CollectionName](jsonToInsert: JsValue): Future[LastError] = {
      collection(implicitly[CollectionName[A]].get).insert(jsonToInsert)
    }


    override def update[A: CollectionName](id:AssetSupport.IdType,json: JsValue): Future[LastError] = {
      val selector = obj(AssetSupport.idFieldName -> id)
      collection(implicitly[CollectionName[A]].get).update(selector,json)
    }
  }



}
