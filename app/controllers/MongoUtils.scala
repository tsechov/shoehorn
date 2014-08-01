package controllers

import play.api.libs.json._
import scala.concurrent.Future
import scala.Some
import play.api.Logger
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import scala.Some
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait MongoUtils {

  def collection:JSONCollection

  def collectionFind[A](filter: Option[JsValue] = None, sort: Option[JsObject] = None)(implicit r: Reads[A], w: Writes[A]): Future[JsArray] = {
    Logger.debug(s"find query json: $filter")



    val filtered = filter match {
      case Some(filterValue) => collection.find(filterValue)
      case None => collection.genericQueryBuilder
    }

    val sorted = sort match {
      case Some(sorter) => filtered.sort(sorter)
      case None => filtered
    }

    val futureList = sorted.cursor[A].collect[List]()

    futureList.map {
      _.foldLeft(JsArray())((acc, elem) => {println(s"elem: $elem");acc ++ Json.arr(elem)})
    }


  }

  def collectionInsert(entity: JsObject) = {
    import LastErrorWrapperImplicits._
    collection.insert(entity).map {
      lastError => lastError.orFail
    }
  }
}
