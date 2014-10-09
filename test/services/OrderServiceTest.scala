package services

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.mockito.Mockito._
import play.api.libs.json.{JsObject, JsArray, Json, JsValue}

import scala.concurrent.Future
import scala.io.Source
import scala.util.Try

class OrderServiceTest extends Specification with Mockito {

  def readJson(path:String) = {
    val jsonSource = Source.fromFile(path)
    val jsonString = jsonSource.mkString
    jsonSource.close
    Json.parse(jsonString)
  }

  "adding numberOfPairs to order item" should {
    "should not be a problem" in {

      val orderRepo=mock[OrderRepositoryInternal]
      val crudServiceMock=mock[CrudServiceInternal]
      val target=new OrderService  with OrderRepositoryComponent with CrudServiceComponent {
        override val orderRepository: OrderRepositoryInternal = orderRepo
        override val crudService: CrudServiceInternal = crudServiceMock
      }


      val orderJson = readJson("public/api/order.json")
      val sizeGroupsJson=readJson("public/api/sizegroup.json")


      when(crudServiceMock.findAll).thenReturn(Future.successful(Try(sizeGroupsJson.as[JsObject])))

      target.orderService.calculateTotal(orderJson.as[JsObject])



      success
    }

  }
}
