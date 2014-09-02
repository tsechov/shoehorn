package services

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.order.{OrderCreate, OrderIn}
import scala.concurrent.Future
import models.AssetSupport.IdType
import scala.util.{Failure, Success, Try}
import play.api.libs.json._
import play.api.libs.json.JsObject
import reactivemongo.core.errors.GenericDatabaseException
import play.api.Logger
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.data.validation.ValidationError

case class JsonErrors(errors: Seq[(JsPath, Seq[ValidationError])]) extends Throwable

trait OrderServiceComponent {


  trait OrderServiceInternal {
    def orderId(): Future[Try[Int]]

    def ensureIndexOnOrderId: Future[Unit]

    def createOrder(create: JsObject): Future[Try[IdType]]

  }

  val orderService: OrderServiceInternal

}

trait OrderService extends OrderServiceComponent {
  this: OrderRepositoryComponent with CrudServiceComponent =>


  override val orderService = new OrderServiceInternal {


    override def orderId = orderRepository.orderId

    override def ensureIndexOnOrderId = orderRepository.ensureIndexOnOrderId

    override def createOrder(create: JsObject) = {

      val res = for {
        oidTried <- orderId
        insertResult <- insert(for (oidValue <- oidTried; oc <- toCreateModel(create)(oidValue)) yield oc)
        result <- insertResult match {
          case Success(_) => Future.successful(insertResult)
          case Failure(GenericDatabaseException(errorString, code)) if (code == 11000) => createOrder(create)
          case f: Failure[IdType] => Future.successful(f)
        }
      } yield result

      res
    }

    private def toCreateModel(json: JsObject)(orderId: Int): Try[OrderCreate] = {
      val res = for {
        orderIdAdded <- json.transform(addorderId(json)(orderId))
        orderNumberAdded <- orderIdAdded.transform(addorderNumber(orderIdAdded)(orderNumberFormat(orderId)))
        create <- orderNumberAdded.validate[OrderCreate]
      } yield create

      res.fold(errors => Failure(JsonErrors(errors)), res => Success(res))


    }

    private def orderNumberFormat(orderId: Int) = {
      val padded = orderId.toString.reverse.padTo[Char, String](4, '0').reverse
      DateTimeFormat.forPattern("yyyyMMdd").print(new DateTime) + "/" + padded
    }

    private def addorderId(json: JsObject)(orderId: Int): Reads[JsObject] = {

      (__).json.update(
        __.read[JsObject].map {
          o => o ++ Json.obj("orderId" -> orderId)
        }
      )
    }

    private def addorderNumber(json: JsObject)(orderNumber: String): Reads[JsObject] = {

      (__).json.update(
        __.read[JsObject].map {
          o => o ++ Json.obj("orderNumber" -> orderNumber)
        }
      )
    }

    private def insert(create: Try[OrderCreate]): Future[Try[IdType]] = {
      create match {
        case Failure(t: Throwable) => Future.successful(Failure[IdType](t))
        case Success(value) => {
          Logger.debug(s"about to insert $value")
          crudService.insert[OrderCreate, OrderIn](value)
        }
      }


    }


  }


}

trait OrderRepositoryComponent {

  trait Repository {
    def orderId(): Future[Try[Int]]

    def ensureIndexOnOrderId: Future[Unit]
  }

  val orderRepository: Repository

}

trait MongoOrderRepository extends OrderRepositoryComponent {
  this: Mongo =>
  override val orderRepository = new Repository {

    override def orderId() = {
      mongo.nextValue[OrderIn]("orderId")
    }

    override def ensureIndexOnOrderId = mongo.ensureIndex[OrderIn]("orderId").map((result) => ())
  }
}
