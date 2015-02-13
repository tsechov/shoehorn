package services.mongo

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsObject
import services.DbQuery

import scala.concurrent.Future

trait MongoRead {
  self: MongoCollection =>


  def findOne(query: DbQuery) = {
    find(query.copy(limit = Some(1))).flatMap {
      case head :: Nil => Future.successful(head)
      case head :: tail => Future.failed[JsObject](new IllegalStateException(s"more than one element in collection[$collectionName}] for query[$query]"))
      case Nil => Future.failed[JsObject](new NoSuchElementException(s"no matching element in collection[$collectionName}] for query[$query]"))
    }
  }

  def find(query: DbQuery) = {


    (query.projection match {
      case None => collection.find(query.query)
      case Some(p) => collection.find(query.query, p)
    }).cursor[JsObject].collect[List](
        query.limit match {
          case Some(limit) => limit
          case None => Int.MaxValue
        }
      )

  }
}
