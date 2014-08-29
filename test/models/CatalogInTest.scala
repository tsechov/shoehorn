package models

import org.specs2.mutable._
import play.api.libs.json._


class CatalogInTest extends Specification {

  import CatalogTestSupport._

  "Catalog" should {

    "should be transformable" in {
      println(Json.prettyPrint(postJson))
      val newDescription = (__).json.update(__.read[JsObject].map {
        root => root ++ Json.obj("description" -> "updated description")
      })
      val result = postJson.transform(newDescription)
      result match {
        case succ: JsSuccess[JsObject] => println(Json.prettyPrint(succ.get)); success
        case error: JsError => println(error); failure
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
