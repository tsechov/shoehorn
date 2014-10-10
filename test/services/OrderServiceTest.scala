package services

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.mockito.Mockito._
import play.api.libs.json._

import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import models.product.SizeGroupIn
import models.AssetSupport.IdType
import models.{AssetUpdateBuilder, AssetCreate}
import scala.util.Success
import scala.util.Failure
import play.api.libs.json.JsObject

class OrderServiceTest extends Specification with Mockito {


  "adding total to order item" should {
    "should not be a problem" in {
      val orderJson = readJson("public/api/order.json")
      val sizeGroupsJson = readJson("public/api/sizegroup.json")

      val orderRepo = mock[OrderRepositoryInternal]
      val crudServiceMock = mock[CrudServiceInternal]
      val target = new OrderService with OrderRepositoryComponent with CrudServiceComponent {
        override val orderRepository: OrderRepositoryInternal = orderRepo
        override val crudService: CrudServiceInternal = crudServiceMock
      }
      val result = target.orderService.calculateTotal(orderJson.as[JsObject], Success(List(sizeGroupsJson.as[JsObject])))

      result match {
        case Success(total) if (total == 414) => success
        case Failure(e) => {
          failure(e.getMessage)
        }
      }


    }

  }

  def readJson(path: String) = {
    val jsonSource = Source.fromFile(path)
    val jsonString = jsonSource.mkString
    jsonSource.close
    Json.parse(jsonString)
  }
}
