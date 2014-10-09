package services

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import play.api.libs.json.{JsArray, Json, JsValue}
import scala.io.Source

class OrderServiceTest extends Specification with Mockito {


  "adding numberOfPairs to order item" should {
    "should not be a problem" in {
      val jsonSource = Source.fromFile("public/api/order.json")
      val jsonString = jsonSource.mkString
      jsonSource.close
      val json = Json.parse(jsonString)

      val catalogs = (json \ "items").as[JsArray].value.map { (item) =>
        val size = (item \ "size").as[Int]
        val quantity = (item \ "quantity").as[Int]
        ((size, quantity), (item \ "product" \\ "catalogs"))
      }


      //catalogs.foldLeft(0)((acc, sizeAndCatalogs) => ???)

      println(catalogs)

      success
    }

  }
}
