package services

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.{Logger, Application}

import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import reactivemongo.core.commands.LastError
import play.api.libs.json.Json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.JsObject
import models.AssetSupport
import reactivemongo.api.indexes.{IndexType, Index}
import scala.util.{Success, Try}


trait Mongo {
  val mongo: MongoDb
}

trait MongoDb {
  def find[A: CollectionName](query: JsObject): Future[List[JsObject]]

  def findAll[A: CollectionName]: Future[List[JsObject]]

  def insert[A: CollectionName](jsonToInsert: JsValue): Future[LastError]

  def update[A: CollectionName](selector: JsObject, json: JsValue): Future[LastError]

  def remove[A: CollectionName](selector: JsObject): Future[LastError]

  def nextValue[A: CollectionName](field: String): Future[Try[Int]]

  def ensureIndex[A: CollectionName](onField: String): Future[Boolean]
}


class RealMongo(implicit app: Application) extends Mongo {

  private val activeQuery = Json.obj(AssetSupport.activeFieldName -> true)

  def collection(name: String): JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection](name)

  override val mongo = new MongoDb {
    def find[A: CollectionName](query: JsObject) = {

      collection(implicitly[CollectionName[A]].get).find(query).cursor[JsObject].collect[List]()


    }

    override def findAll[A: CollectionName] = find(activeQuery)

    override def insert[A: CollectionName](jsonToInsert: JsValue) = collection(implicitly[CollectionName[A]].get).insert(jsonToInsert)

    override def update[A: CollectionName](selector: JsObject, json: JsValue) = {
      val updateValue = Json.obj("$set" -> json)
      collection(implicitly[CollectionName[A]].get).update(selector, updateValue)

    }

    override def remove[A: CollectionName](selector: JsObject) = collection(implicitly[CollectionName[A]].get).remove(selector)

    override def nextValue[A: CollectionName](field: String) = {
      val sort = Json.obj(field -> -1)
      val lastId: Future[List[JsObject]] = collection(implicitly[CollectionName[A]].get).find[JsObject, JsObject](activeQuery, Json.obj(field -> 1)).sort(sort).cursor[JsObject].collect[List](1)
      val nextId = lastId.map {
        docs => Success(if (docs.isEmpty) 1
        else {
          Logger.debug(s"lastiddoc: ${docs.head}")
          (docs.head \ field).as[Int] + 1
        })

      }
      nextId
    }

    override def ensureIndex[A: CollectionName](onField: String) = collection(implicitly[CollectionName[A]].get).indexesManager.ensure(Index(Seq(onField -> IndexType.Ascending)))
  }


}
