package services

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Application
import scala.concurrent.Future
import play.api.libs.json.{Reads, JsObject}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.json.collection.JSONCollection



class MongoRepository[A](collectionName:String)(implicit app: Application) extends MongoComponent[A]{
  /** Returns the current instance of the driver. */
  def driver = ReactiveMongoPlugin.driver
  /** Returns the current MongoConnection instance (the connection pool manager). */
  def connection = ReactiveMongoPlugin.connection
  /** Returns the default database (as specified in `application.conf`). */
  def db = ReactiveMongoPlugin.db

  def collection: JSONCollection = db.collection[JSONCollection](collectionName)

  override val mongo = new Mongo[A] {
    def find(query:JsObject)(implicit r:Reads[A]):Future[List[A]] = {
      collection.find(query).cursor[A].collect[List]()
    }
  }

}
