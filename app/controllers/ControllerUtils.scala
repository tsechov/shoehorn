package controllers

import play.api.{Logger, Play}
import play.api.libs.json.{Writes, Json, JsArray}
import play.api.http.ContentTypes
import play.api.mvc.{Results, Controller}
import scala.concurrent.Future


trait ControllerUtils {
  self:Results =>
  val contextUrl = Play.application(play.api.Play.current).configuration.getString("context.url").getOrElse("")
  def toJsArray[A](list:List[A])(implicit w:Writes[A]):JsArray= list.foldLeft(JsArray())((acc, elem) => acc ++ Json.arr(elem))
  def foundOrNot[A](resultList:List[A])(implicit w:Writes[A]) = if (resultList.isEmpty) NotFound else Ok(toJsArray(resultList)).as(ContentTypes.JSON)
  def badQuery(query:String, error:Throwable) = {
    Logger.debug(s"error parsing query: $query")
    Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
  }

}
