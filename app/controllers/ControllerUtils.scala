package controllers

import play.api.{Logger, Play}
import play.api.libs.json._
import play.api.http.ContentTypes
import play.api.mvc.{SimpleResult, Call, Results, Controller}
import scala.concurrent.Future
import scala.util._
import play.api.libs.json.Json._
import play.api.mvc.Call
import play.api.mvc.SimpleResult
import play.api.mvc.Call
import play.api.libs.json.JsArray
import play.api.mvc.SimpleResult
import models.AssetSupport
import play.api.mvc.Call
import play.api.libs.json.JsArray
import play.api.libs.json.JsSuccess
import scala.util.Failure
import play.api.mvc.SimpleResult


trait ControllerUtils {
  self: Results =>
  val contextUrl = Play.application(play.api.Play.current).configuration.getString("context.url").getOrElse("")

  def locationUrl(id: String, route: String => Call) = contextUrl + route(id).toString

  def toJsArray[A](list: List[A])(implicit w: Writes[A]): JsArray = list.foldLeft(JsArray())((acc, elem) => acc ++ Json.arr(elem))

  def foundOrNot[A](resultList: List[A])(implicit w: Writes[A]) = if (resultList.isEmpty) NotFound else Ok(toJsArray(resultList)).as(ContentTypes.JSON)

  def badQuery(query: String, error: Throwable) = {
    Logger.debug(s"error parsing query: $query")
    Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
  }


  def internalServerError[A](msg: String): PartialFunction[Try[A], SimpleResult] = {
    case Failure(error) => {
      Logger.debug(s"$msg: $error")
      InternalServerError
    }
  }

  def badJsonRequest[A](msg: String): PartialFunction[JsResult[A], Future[SimpleResult]] = {
    case error: JsError => {
      val jsonError = JsError.toFlatJson(error)
      Logger.debug(s"$msg: $jsonError")
      Future.successful(BadRequest(jsonError))
    }
  }

  def performOperation[A, R](operationName: String, operation: A => Future[Try[R]], successfulResult: (R, String) => SimpleResult): PartialFunction[JsResult[A], Future[SimpleResult]] = {
    badJsonRequest[A](s"[$operationName] invalid input json") orElse {
    case jsSuccess: JsSuccess[A] => {
      val futureResult = operation(jsSuccess.get)

      import play.api.libs.concurrent.Execution.Implicits.defaultContext
      futureResult.map {
        internalServerError[R](s"[$operationName] error") orElse {
          case Success(r) => successfulResult(r, s"[$operationName] successful")
        }
      }
    }
    }

  }

}
