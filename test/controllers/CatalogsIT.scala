package controllers

import scala.concurrent._

import play.api.libs.json._

import play.api.test._
import play.api.test.Helpers._
import models.CatalogTestSupport._
import play.api.http.{Writeable, ContentTypes, HeaderNames}
import org.joda.time.DateTime

import play.api.mvc.{Request, AnyContent, SimpleResult}

import controllers.routes.Catalogs
import models.{CatalogPaths, DateFormatSupport}

class CatalogsIT extends CommonControllerSpecs with DateFormatSupport with CatalogPaths{


  def checkDateField(jsValue: JsValue, path: JsPath) = {
    val createdAt = jsValue.transform(path.json.pick)
    createdAt must beAnInstanceOf[JsSuccess[JsValue]]
    Json.fromJson(createdAt.get)(dateFormat) must beAnInstanceOf[JsSuccess[DateTime]]
  }

  "Catalogs controller" should {

    "insert and get a valid json by http post" in {
      running(FakeApplication()) {

        val result = corsRequest(FakeRequest(POST, Catalogs.create.toString).withJsonBody(postJson), CREATED)

        result.header.headers.keySet must contain(HeaderNames.LOCATION)

        val location = result.header.headers(HeaderNames.LOCATION)

        val expectedId = location.stripPrefix(location.take(location.lastIndexOf('/') + 1))

        val getResult = corsRequest(FakeRequest(GET, Catalogs.getById(expectedId).toString), OK)

        val jsonResult = contentAsJson(Future.successful(getResult))

        checkDateField(jsonResult, createdAtPath)
        checkDateField(jsonResult, lastModifiedAtPath)

        jsonResult.transform(idPath.json.prune andThen createdAtPath.json.prune andThen lastModifiedAtPath.json.prune).getOrElse(Json.obj()) must beEqualTo(postJson)

      }
    }

    "fail inserting a non valid json" in {
      running(FakeApplication()) {
        val request = FakeRequest.apply(POST, Catalogs.create.toString).withJsonBody(Json.obj(
          "firstName" -> 98,
          "lastName" -> "London",
          "age" -> 27))
        val response = route(request)
        response.isDefined mustEqual true
        val result = Await.result(response.get, timeout)

        val jsonResponse = contentAsJson(response.get)

        result.header.status mustEqual BAD_REQUEST
      }
    }

  }

}