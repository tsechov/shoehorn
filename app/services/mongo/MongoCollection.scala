package services.mongo

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.Play.current

/**
 * @author tsechov
 */
trait MongoCollection {
  def collectionName: String

  protected def collection: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection](collectionName)


}
