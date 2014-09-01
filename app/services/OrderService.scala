package services

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.order.OrderIn
import scala.concurrent.Future
import models.AssetSupport.IdType
import scala.util.Try
import play.api.libs.json._
import play.api.libs.json.JsObject

trait OrderServiceComponent {

  trait Service {
    def orderNumber(): Future[Int]

    def ensureIndexOnOrderNumber: Future[Unit]

    def createOrder(create: JsObject): Future[Try[IdType]]

  }

  val orderService: Service

}

trait OrderService extends OrderServiceComponent {
  this: OrderRepositoryComponent with CrudServiceComponent =>

  override val orderService = new Service {


    override def orderNumber = orderRepository.orderNumber

    override def ensureIndexOnOrderNumber = orderRepository.ensureIndexOnOrderNumber

    override def createOrder(create: JsObject) = {
//      for {
      //        on<-orderNumber
      //        transformed <- create.transform(addOrderNumber(create)(on))
      //      }

      ???
    }

    private def addOrderNumber(json: JsObject)(orderNumber: Int): Reads[JsObject] = {

      (__).json.update(
        __.read[JsObject].map {
          o => o ++ Json.obj("orderNumber" -> orderNumber)
        }
      )
    }


  }


}

trait OrderRepositoryComponent {

  trait Repository {
    def orderNumber(): Future[Int]

    def ensureIndexOnOrderNumber: Future[Unit]
  }

  val orderRepository: Repository

}

trait MongoOrderRepository extends OrderRepositoryComponent {
  this: Mongo =>
  override val orderRepository = new Repository {

    override def orderNumber() = {
      mongo.nextValue[OrderIn]("orderNumber")
    }

    override def ensureIndexOnOrderNumber = mongo.ensureIndex[OrderIn]("orderNumber").map((result) => ())
  }
}
