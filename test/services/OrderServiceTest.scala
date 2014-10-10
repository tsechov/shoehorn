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
import scala.concurrent.duration.FiniteDuration

class OrderServiceTest extends Specification with Mockito {

  val orderJson = readJson("public/api/order.json")
  val sizeGroupsJson = readJson("public/api/sizegroup.json")

  val timeout = FiniteDuration(5, TimeUnit.SECONDS)

  "OrderService" should {
    "be able to calculate total for order" in {

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
//    "be able to create order" in {
//      val orderRepo = mock[OrderRepositoryInternal]
//
//      val target = new OrderService with OrderRepositoryComponent with CrudServiceComponent {
//        override val orderRepository: OrderRepositoryInternal = orderRepo
//        override val crudService: CrudServiceInternal = crudServiceStub(Future.successful(Success(List(sizeGroupsJson.as[JsObject]))))
//      }
//
//
//
//      val result = target.orderService.createOrder(orderJson.as[JsObject])
//
//      Await.result(result, timeout) match {
//        case Success(value) => success
//        case _ => failure
//      }
//
//    }

  }

  def readJson(path: String) = {
    val jsonSource = Source.fromFile(path)
    val jsonString = jsonSource.mkString
    jsonSource.close
    Json.parse(jsonString)
  }

  def crudServiceStub(findAll: Future[Try[List[JsObject]]]) = new CrudServiceInternal {
    override def update[A <: AssetUpdateBuilder[U], U](id: IdType)(input: A)(implicit w: Writes[U], ev: CollectionName[U]) = ???

    override def insert[C <: AssetCreate[A], A](input: C)(implicit w: Writes[A], ev: CollectionName[A]) = ???

    override def findAll[A: CollectionName] = findAll

    override def remove[A: CollectionName](id: IdType) = ???

    override def getById[A: CollectionName](id: IdType) = ???

    override def find[A: CollectionName](query: DbQuery) = ???
  }
}
