package services.mongo

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.Reads
import services.DbQuery

import scala.concurrent.Future

trait MongoRead {
  self: MongoCollection =>


  def findOne[A](query: DbQuery)(implicit fjs: Reads[A]): Future[A] = {
    find[A](query.copy(limit = Some(1))).flatMap {
      case head :: Nil => Future.successful(head)
      case head :: tail => Future.failed[A](new IllegalStateException(s"more than one element in collection[$collectionName}] for query[$query]"))
      case Nil => Future.failed[A](new NoSuchElementException(s"no matching element in collection[$collectionName}] for query[$query]"))
    }
  }

  def find[A](query: DbQuery)(implicit fjs: Reads[A]) = {


    (query.projection match {
      case None => collection.find(query.query)
      case Some(p) => collection.find(query.query, p)
    }).cursor[A].collect[List](
        query.limit match {
          case Some(limit) => limit
          case None => Int.MaxValue
        }
      )

  }
}
