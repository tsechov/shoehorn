package models

import org.specs2.mutable._
import play.api.libs.json._
import org.joda.time.DateTime
import models.catalog.CatalogCreate


class CatalogTest extends Specification {

  import CatalogTestSupport._

  "Catalog" should {
    "should be readable from json without id" in {


      val expectedId: String = "123"
      val expectedDate = new DateTime

      println(Json.prettyPrint(Json.toJson(new DateTime("1952-03-11"))))
      println(Json.prettyPrint(postJson))
      val transformer = AssetTransform.create(postJson)(expectedId, expectedDate)

      val result = for {
        validated <- {
          println(Json.prettyPrint(postJson)); postJson.validate[CatalogCreate]
        }
        transformed <- postJson.transform(transformer)

      } yield transformed

      result match {
        case c: JsSuccess[JsObject] => c.get \ AssetSupport.idFieldName must equalTo(JsString(expectedId))
        case e: JsError => println(e); 1 must equalTo(0)
      }


    }
    "should be transformable" in {
      println(Json.prettyPrint(postJson))
      val newDescription=(__).json.update(__.read[JsObject].map{root => root ++ Json.obj("description"->"updated description")})
      val result=postJson.transform(newDescription)
      result match {
        case succ:JsSuccess[JsObject] => println(Json.prettyPrint(succ.get));success
        case error:JsError => println(error);failure
      }
    }

}

}

object CatalogTestSupport {

  val postJson: JsValue = Json.parse( s"""
      {
        "active": true,
        "description": "blah",

        "year" : 2014,
        "season": "fall",
        "status": true,
        "webStatus": false
      }""")

}
