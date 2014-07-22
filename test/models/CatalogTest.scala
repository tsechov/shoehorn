package models

import org.specs2.mutable._
import play.api.libs.json._
import org.joda.time.DateTime

class CatalogTest extends Specification {

  import CatalogTestSupport._
  "Catalog" should {
    "should be readable from json without id" in {



      val expectedId: String = "123"
      val expectedDate=new DateTime
      import models.AssetModelSupport._
      println(Json.prettyPrint(Json.toJson(new DateTime("1952-03-11"))))
      println(Json.prettyPrint(postJson))
      val transformer=Catalog.createTransformer(postJson)(expectedId,expectedDate)

      val result = for {
        transformed <- postJson.transform(transformer)
        validated <- {println(Json.prettyPrint(transformed)); transformed.validate[Catalog]}
      } yield validated

      result match {
        case c: JsSuccess[Catalog] => c.get.id must beSome(expectedId)
        case e:JsError => println(e);1 must equalTo(0)
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
