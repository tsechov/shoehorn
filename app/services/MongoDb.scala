package services

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Application

import play.api.libs.json.{JsValue, Json, Reads}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.JsObject
import scala.concurrent.Future
import reactivemongo.core.commands.LastError


trait Mongo {
  val mongo: MongoDb
}

trait MongoDb {
  def find[A](query: JsObject)(implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]

  def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[List[A]]

  def insert[A: CollectionName](jsonToInsert: JsValue): Future[LastError]

  def update[A: CollectionName](selector: JsObject, json: JsValue): Future[LastError]

  def remove[A: CollectionName](selector: JsObject): Future[LastError]
}


class RealMongo(implicit app: Application) extends Mongo {

  private val emptyQuery = Json.obj()

  def collection(name: String): JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection](name)

  override val mongo = new MongoDb {
    def find[A](query: JsObject)(implicit r: Reads[A], ev: CollectionName[A]) = collection(ev.get).find(query).cursor[A].collect[List]()
    
    override def findAll[A](implicit r: Reads[A], ev: CollectionName[A]) = find(emptyQuery)

    override def insert[A: CollectionName](jsonToInsert: JsValue) = collection(implicitly[CollectionName[A]].get).insert(jsonToInsert)

    override def update[A: CollectionName](selector: JsObject, json: JsValue) = collection(implicitly[CollectionName[A]].get).update(selector, json)

    override def remove[A: CollectionName](selector: JsObject) = collection(implicitly[CollectionName[A]].get).remove(selector)

  }


}
