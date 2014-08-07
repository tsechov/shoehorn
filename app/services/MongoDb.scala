package services

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Application

import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.core.commands.LastError
import play.api.libs.json.Json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.JsObject
import scala.util.Try


trait Mongo {
  val mongo: MongoDb
}

trait MongoDb {
  def find[A](query: JsObject)(implicit r: Reads[A], ev: CollectionName[A]): Future[Try[List[A]]]

  def findAll[A](implicit r: Reads[A], ev: CollectionName[A]): Future[Try[List[A]]]

  def insert[A: CollectionName](jsonToInsert: JsValue): Future[LastError]

  def update[A: CollectionName](selector: JsObject, json: JsValue): Future[LastError]

  def remove[A: CollectionName](selector: JsObject): Future[LastError]
}


class RealMongo(implicit app: Application) extends Mongo {

  private val emptyQuery = Json.obj()

  def collection(name: String): JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection](name)

  override val mongo = new MongoDb {
    def find[A](query: JsObject)(implicit r: Reads[A], ev: CollectionName[A]) = {

      collection(ev.get).find(query).cursor[JsObject].collect[List]().map(
        list => Try(list.map {
          _.validate[A] match {
            case JsSuccess(elem, _) => elem
            case JsError(error) => throw new IllegalArgumentException(s"error reading collection [" + ev.get + "]: " + JsError.toFlatJson(error).toString)
          }
        })
      )
    }

    override def findAll[A](implicit r: Reads[A], ev: CollectionName[A]) = find(emptyQuery)

    override def insert[A: CollectionName](jsonToInsert: JsValue) = collection(implicitly[CollectionName[A]].get).insert(jsonToInsert)

    override def update[A: CollectionName](selector: JsObject, json: JsValue) = {
      val updateValue = Json.obj("$set" -> json)
      collection(implicitly[CollectionName[A]].get).update(selector, updateValue)

    }

    override def remove[A: CollectionName](selector: JsObject) = collection(implicitly[CollectionName[A]].get).remove(selector)

  }


}
