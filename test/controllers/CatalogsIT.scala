package controllers

import scala.concurrent._
import duration._
import org.specs2.mutable._

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.test._
import play.api.test.Helpers._
import java.util.concurrent.TimeUnit
import models.CatalogTestSupport._
import play.api.http.{Writeable, ContentTypes, HeaderNames}
import org.joda.time.DateTime
import models.AssetModelSupport._
import play.api.mvc.{Request, AnyContent, SimpleResult}
import play.api.libs.iteratee.Iteratee


class CatalogsIT extends Specification {

  val timeout: FiniteDuration = FiniteDuration(5, TimeUnit.SECONDS)

  def checkDateField(jsValue: JsValue, path: JsPath) = {
    val createdAt = jsValue.transform(path.json.pick)
    createdAt must beAnInstanceOf[JsSuccess[JsValue]]
    Json.fromJson(createdAt.get)(dateFormat) must beAnInstanceOf[JsSuccess[DateTime]]
  }

  def request[T](request: Request[T], exptectedStatus: Int)(implicit w: Writeable[T]): SimpleResult = {
    val response = route(request)
    response.isDefined mustEqual true
    val result = Await.result(response.get, timeout)
    result.header.status must equalTo(exptectedStatus)
    result
  }

  "Catalogs" should {

    "insert and get a valid json by http post" in {
      running(FakeApplication()) {

        val result = request(FakeRequest(POST, controllers.routes.Catalogs.create.toString).withJsonBody(postJson), CREATED)

        result.header.headers.keySet must contain(HeaderNames.LOCATION)

        val location = result.header.headers(HeaderNames.LOCATION)

        val expectedId = location.stripPrefix(location.take(location.lastIndexOf('/') + 1))

        val getResult = request(FakeRequest(GET, controllers.routes.Catalogs.getById(expectedId).toString), OK)

        val jsonResult = contentAsJson(Future.successful(getResult))

        checkDateField(jsonResult, createdAtPath)
        checkDateField(jsonResult, lastModifiedAtPath)

        jsonResult.transform(idPath.json.prune andThen createdAtPath.json.prune andThen lastModifiedAtPath.json.prune).getOrElse(Json.obj()) must beEqualTo(postJson)

      }
    }

    "fail inserting a non valid json" in {
      running(FakeApplication()) {
        val request = FakeRequest.apply(POST, controllers.routes.Catalogs.create.toString).withJsonBody(Json.obj(
          "firstName" -> 98,
          "lastName" -> "London",
          "age" -> 27))
        val response = route(request)
        response.isDefined mustEqual true
        val result = Await.result(response.get, timeout)
        contentAsString(response.get) mustEqual "invalid json"
        result.header.status mustEqual BAD_REQUEST
      }
    }

  }

}