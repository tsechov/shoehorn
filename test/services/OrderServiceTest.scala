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
        override val crudService: CrudServiceInternal = new CrudServiceInternal {
          override def update[A <: AssetUpdateBuilder[U], U](id: IdType)(input: A)(implicit w: Writes[U], ev: CollectionName[U]) = ???

          override def insert[C <: AssetCreate[A], A](input: C)(implicit w: Writes[A], ev: CollectionName[A]) = ???

          override def findAll[A: CollectionName] = Future.successful(Try(List(sizeGroupsJson.as[JsObject])))

          override def remove[A: CollectionName](id: IdType) = ???

          override def getById[A: CollectionName](id: IdType) = ???

          override def find[A: CollectionName](query: DbQuery) = ???
        }
      }




      when(crudServiceMock.findAll[SizeGroupIn]).thenReturn(Future.successful(Success(List(sizeGroupsJson.as[JsObject]))))

      val result = target.orderService.calculateTotal(orderJson.as[JsObject])

      Await.result(result, FiniteDuration(5, TimeUnit.SECONDS)) match {
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
