package services

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.order.{OrderCreate, OrderIn}
import scala.concurrent.Future
import models.AssetSupport.IdType
import scala.util.{Failure, Success, Try}
import play.api.libs.json._
import play.api.libs.json.JsObject
import reactivemongo.core.errors.GenericDatabaseException

trait OrderServiceComponent {

  trait Service {
    def orderNumber(): Future[Try[Int]]

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

      val res = for {
        onTried <- orderNumber
        insertResult <- insert(for (onValue <- onTried; oc <- toCreateModel(create)(onValue)) yield oc)
        result <- insertResult match {
          case Success(_) => Future.successful(insertResult)
          case Failure(GenericDatabaseException(errorString, code)) if (code == 11000) => createOrder(create)
          case f: Failure[IdType] => Future.successful(f)
        }
      } yield result

      res
    }

    private def toCreateModel(json: JsObject)(orderNumber: Int): Try[OrderCreate] = {
      val res = for {
        transformed <- json.transform(addOrderNumber(json)(orderNumber))
        create <- transformed.validate[OrderCreate]
      } yield create
      res match {
        case JsSuccess(value, _) => Success(value)
        case e => {
          Failure(new IllegalArgumentException(e.toString))
        }
      }


    }

    private def addOrderNumber(json: JsObject)(orderNumber: Int): Reads[JsObject] = {

      (__).json.update(
        __.read[JsObject].map {
          o => o ++ Json.obj("orderNumber" -> orderNumber)
        }
      )
    }

    private def insert(create: Try[OrderCreate]): Future[Try[IdType]] = {
      create match {
        case Failure(t: Throwable) => Future.successful(Failure[IdType](t))
        case Success(value) => crudService.insert[OrderCreate, OrderIn](value)
      }


    }


  }


}

trait OrderRepositoryComponent {

  trait Repository {
    def orderNumber(): Future[Try[Int]]

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
