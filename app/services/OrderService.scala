package services

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.order._
import scala.concurrent.{Await, Future}
import models.AssetSupport.IdType
import scala.util.Try
import play.api.libs.json._
import play.api.Logger
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import models.customer.{CustomerIn, AgentIn}
import scala.concurrent.duration._
import services.reporting._
import scalax.file.FileSystem
import play.api.libs.json.JsArray
import scala.util.Failure
import scala.Some
import play.api.data.validation.ValidationError
import models.order.SortimentItem
import scala.util.Success
import models.order.AgentReport
import models.order.CustomerReport
import reactivemongo.core.errors.GenericDatabaseException
import services.reporting.XmlParameterExpression
import play.api.libs.json.JsObject

case class JsonErrors(errors: Seq[(JsPath, Seq[ValidationError])]) extends Throwable

trait OrderServiceComponent {


  trait OrderServiceInternal {
    def orderId(): Future[Try[Int]]

    def ensureIndexOnOrderId: Future[Unit]

    def createOrder(create: JsObject): Future[Try[IdType]]

  }

  trait OrderPrintServiceInternal {
    def getPdf(orderId: IdType): Future[Try[Option[Array[Byte]]]]
  }

  val orderService: OrderServiceInternal
  val orderPrintService: OrderPrintServiceInternal

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

  override val orderPrintService = new OrderPrintServiceInternal with ReportFormats {
    override def getPdf(orderId: IdType): Future[Try[Option[Array[Byte]]]] = {
      val order = crudService.getById[OrderIn](orderId)



      order.map {
        _.map {
          _.flatMap {
            orderJson => {
              val deadlinesIds = (orderJson \ "deadlines").as[JsArray].value.map(v => (v \ "deadlineTypeId").as[String])
              val deadlineNames = getDeadlines(deadlinesIds)

              val agentId = (orderJson \ "originatorId").as[IdType]
              println(agentId)
              val agent = crudService.getById[AgentIn](agentId)
              val customerId = (orderJson \ "customerId").as[IdType]
              val customer = crudService.getById[CustomerIn](customerId)


              val ff = for {
                d <- deadlineNames
                a <- agent
                c <- customer
              } yield for {
                  dd <- d
                  aa <- a
                  cc <- c
                } yield for {
                    aaa <- aa
                    ccc <- cc
                  } yield binReport(mapOrderPrint(dd, aaa, ccc, orderJson))


              Await.result(ff, 20 seconds) match {
                case Success(pdf) => pdf
                case Failure(e) => throw e
              }


            }

          }

        }
      }
    }

    private def mapOrderPrint(deadlineTypes: Map[IdType, String], agent: JsObject, customer: JsObject, order: JsObject): OrderReport = {
      val address = (order \ "billingAddress" \ "country").as[String]
      val shippingName = "shipping name"
      val shippingAddress = "shipping address"
      val customer = CustomerReport("name", address, shippingName, shippingAddress, "adoszam123", "bankszamlaszamjool")
      val agent = AgentReport("neve", "payment", "phonenumber123", email = "foo@bar.com")
      val sortiment1 = List(SortimentItem(18, 2), SortimentItem(24, 1), SortimentItem(34, 5))
      val items1 = ProductReport("1011-22432", "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1011-22432-full.jpg", sortiment1)
      val sortiment2 = List(SortimentItem(19, 2), SortimentItem(24, 1), SortimentItem(34, 5))
      val items2 = ProductReport("1127-22182", "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1127-22182-full.jpg", sortiment2)
      OrderReport("idjool", "ordernumber", new DateTime, Some(new DateTime), Some(new DateTime), None, None, None, customer, agent, List(items1, items2), 16)

    }

    private def binReport(report: OrderReport): Array[Byte] = {
      def writeReport(report: OrderReport): String = {

        val tmpFile = FileSystem.default.createTempFile(suffix = ".shoehorn-order.xml")
        val xml = ToXml.get(report)
        tmpFile.outputStream().write(xml)
        tmpFile.toAbsolute.path
      }

      val datasourcefile = writeReport(report)

      val runner = new ReportRunner with JRXmlReportCompiler with ClasspathResourceReportLoader
      runner.runReportT("/order2.jrxml")(PdfDS, EmptyExpression, XmlParameterExpression(datasourcefile, "/order/items/product")).run.toEither match {
        case Left(e) => throw new RuntimeException("cannot generate report", e)
        case Right(pdf) => pdf
      }
    }

    private def getDeadlines(ids: Seq[IdType]): Future[Try[Map[IdType, String]]] = {
      val exprs = ids.foldLeft(JsArray())((array, id) => array :+ Json.obj("_id" -> id))
      val query = Json.obj("$or" -> exprs)
      Logger.debug(s"deadlines query: $query")
      val ds = crudService.find[DeadlineTypeIn](query)
      ds.map {
        _.map {
          jsonList => {
            println(jsonList)
            val res = jsonList.foldLeft(Map[IdType, String]())(
              (map, json) => {
                Logger.debug(s"deadlines: $json")
                map ++ Map((json \ "_id").as[IdType] -> (json \ "name").as[String])
              }
            )
            println(res)
            res
          }
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

    override def ensureIndexOnOrderId = mongo.ensureUniqueIndex[OrderIn]("orderId").map((result) => ())
  }
}
