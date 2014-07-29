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

class CatalogsIT extends CommonControllerSpecs with DateFormatSupport with CatalogPaths {


  def checkDateField(jsValue: JsValue, path: JsPath) = {
    val createdAt = jsValue.transform(path.json.pick)
    createdAt must beAnInstanceOf[JsSuccess[JsValue]]
    Json.fromJson(createdAt.get)(dateFormat) must beAnInstanceOf[JsSuccess[DateTime]]
  }

  def idExtract(result:SimpleResult) = {
    val location = result.header.headers(HeaderNames.LOCATION)

    location.stripPrefix(location.take(location.lastIndexOf('/') + 1))

  }

  def create = {
    val result = corsRequest(FakeRequest(POST, Catalogs.create.toString).withJsonBody(postJson), CREATED)

    result.header.headers.keySet must contain(HeaderNames.LOCATION)

    result

  }

  def get(id:String) = {
    val getResult = corsRequest(FakeRequest(GET, Catalogs.getById(id).toString), OK)

    contentAsJson(Future.successful(getResult))
  }

  "Catalogs controller" should {

    "create a catalog using valid json by http post" in {
      running(FakeApplication()) {

        val jsonResult = get((idExtract(create)))

        checkDateField(jsonResult, createdAtPath)
        checkDateField(jsonResult, lastModifiedAtPath)

        jsonResult.transform(idPath.json.prune andThen createdAtPath.json.prune andThen lastModifiedAtPath.json.prune).getOrElse(Json.obj()) must beEqualTo(postJson)

      }
    }

    "update a catalog using valid json by http put" in {
      running(FakeApplication()) {

        val expectedId = idExtract(create)

        val expectedDescription=Json.obj("description"->"updated description")

        val newDescription=(__).json.update(__.read[JsObject].map{root => root ++ expectedDescription})
        val updateResult=corsRequest(FakeRequest(PUT, Catalogs.update(expectedId).toString).withJsonBody(postJson.transform(newDescription).get), OK)

        success
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