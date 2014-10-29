package services

import java.io.ByteArrayInputStream
import java.util.UUID

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.order._

import services.storage.StorageComponent
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
import models.DateFormatSupport
import models.product.SizeGroupIn

case class JsonErrors(errors: Seq[(JsPath, Seq[ValidationError])]) extends Throwable

sealed trait OrderActionMessage{
  def id:UUID
  def orderId:IdType
  def storageFolder:String
  def storageKey=s"$storageFolder/$orderId$id"
  def url(bucketName:String)=s"https://${bucketName}.s3.amazonaws.com/$storageKey"
}
case class OrderReportRequest(id: UUID, orderId: IdType,storageFolder:String) extends OrderActionMessage

case class OrderMailRequest(id:UUID,orderId:IdType,storageFolder:String) extends OrderActionMessage

case class OrderReportContainer(agent: JsObject, customer: JsObject, order: JsObject,bytes:Array[Byte])

trait OrderPrintServiceInternal {
  def getPdf(orderId: IdType): Future[Try[Option[OrderReportContainer]]]
  def storePdf(req:OrderActionMessage): Future[Try[Option[String]]]
  def storeInternal(objectKey: String, stream: ByteArrayInputStream):Option[String]

}
trait OrderServiceInternal {
  def orderId(): Future[Try[Int]]

  def ensureIndexOnOrderId: Future[Unit]

  def createOrder(create: JsObject): Future[Try[IdType]]

  def updateOrder(id: IdType, update: JsObject): Future[Try[Unit]]

}

trait OrderServiceComponent {

  val orderService: OrderServiceInternal
  val orderPrintService: OrderPrintServiceInternal

}

trait OrderService extends OrderServiceComponent {
  this: OrderRepositoryComponent with CrudServiceComponent with StorageComponent=>


  override val orderService = new OrderServiceInternal {


    override def orderId = orderRepository.orderId

    override def ensureIndexOnOrderId = orderRepository.ensureIndexOnOrderId

    override def createOrder(create: JsObject) = {

      for {
        sgs <- crudService.findAll[SizeGroupIn]
        oidTried <- orderId
        insertResult <- insert(for (total <- calculateTotal(create, sgs); numberOfPairs <- calculateNumberOfPairs(create); oidValue <- oidTried; oc <- toCreateModel(create)(oidValue, total, numberOfPairs)) yield oc)
        result <- insertResult match {
          case Success(_) => Future.successful(insertResult)
          case Failure(GenericDatabaseException(errorString, code)) if (code == 11000) => createOrder(create)
          case f: Failure[IdType] => Future.successful(f)
        }
      } yield result


    }


    override def updateOrder(id: IdType, update: JsObject) = {

      for {
        sgs <- crudService.findAll[SizeGroupIn]
        updateResult <- updateInternal(id, for (total <- calculateTotal(update, sgs); numberOfPairs <- calculateNumberOfPairs(update); op <- toUpdateModel(update, total, numberOfPairs)) yield op)
        result <- updateResult match {
          case Success(_) => Future.successful(updateResult)
          case f:Failure[Unit] => Future.successful(f)
        }
      } yield result


    }

    private def toUpdateModel(json: JsObject, total: Int, numberOfPairs: Int): Try[OrderIn] = {
      val res = for {
        totalAdded <- addTotal(json, total)
        numberOfPairsAdded <- addNumberOfPairs(totalAdded, numberOfPairs)
        update <- numberOfPairsAdded.validate[OrderIn]
      } yield update

      res.fold(errors => Failure(JsonErrors(errors)), res => Success(res))

    }

    private def updateInternal(id: IdType, update: Try[OrderIn]): Future[Try[Unit]] = {
      update match {
        case Failure(t: Throwable) => Future.successful(Failure[Unit](t))
        case Success(value) => {
          Logger.debug(s"about to update $value")
          crudService.update[OrderIn, OrderUpdate](id)(value)
        }

      }
    }


    private def toCreateModel(json: JsObject)(orderId: Int, total: Int, numberOfPairs: Int): Try[OrderCreate] = {

      val res = for {

        orderIdAdded <- json.transform(addToRoot(json)("orderId", orderId))
        orderNumberAdded <- orderIdAdded.transform(addToRoot(orderIdAdded)("orderNumber", orderNumberFormat(orderId)))
        totalAdded <- addTotal(orderNumberAdded, total)
        numberOfPairsAdded <- addNumberOfPairs(totalAdded, numberOfPairs)
        create <- numberOfPairsAdded.validate[OrderCreate]
      } yield create

      res.fold(errors => Failure(JsonErrors(errors)), res => Success(res))


    }

    private def addTotal(order: JsObject, total: Int) = {
      order.transform(addToRoot(order)("total", total))
    }

    private def addNumberOfPairs(order: JsObject, numberOfPairs: Int) = {
      order.transform(addToRoot(order)("numberOfPairs", numberOfPairs))
    }

    private def orderNumberFormat(orderId: Int) = {
      val padded = orderId.toString.reverse.padTo[Char, String](4, '0').reverse
      DateTimeFormat.forPattern("yyyyMMdd").print(new DateTime) + "/" + padded
    }


    private def addToRoot[A](json: JsObject)(fieldName: String, value: A)(implicit w: Writes[A]) = {
      (__).json.update(
        __.read[JsObject].map {
          o => o ++ Json.obj(fieldName -> value)
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

    def calculateTotal(order: JsObject, sgs: Try[List[JsObject]]): Try[Int] = {

      //      val sgss = crudService.findAll[SizeGroupIn]
      //      val result = sgss.map {
      case class SizeAndCatalog(productId: IdType, size: Int, quantity: Int, firstCatalog: JsValue)
      sgs.flatMap {
        (sizeGroups) => {

          val itemSizesAndCatalogs = (order \ "items").as[JsArray].value.map { (item) =>
            val size = (item \ "size").as[Int]
            val quantity = (item \ "quantity").as[Int]
            val firstCatalog = (item \ "product" \ "catalogs").as[JsArray].value.headOption.getOrElse(Json.obj())
            SizeAndCatalog((item \ "product" \ "_id").as[IdType], size, quantity, firstCatalog)
          }




          itemSizesAndCatalogs.foldLeft(Try(0))((acc, sizeAndCatalog) => {

            val targetSizeGroups = findSizeGroupIdBySize(sizeGroups, sizeAndCatalog.size)
//            targetSizeGroupOpt match {
//              case Some(targetSizeGroup) => {
                val unitPriceOpt = (sizeAndCatalog.firstCatalog \ "sizeGroups").as[JsArray].value.collectFirst {
                  case g: JsObject if (targetSizeGroups.contains((g \ "sizeGroupId").as[IdType])) => {
                    (g \ "unitPrice").asOpt[Int]
                  }
                }

                unitPriceOpt.flatten match {
                  case Some(unitPrice) if (acc.isSuccess) => Success(acc.get + (unitPrice * sizeAndCatalog.quantity))
                  case _ if (acc.isFailure) => acc
                  case _ => Failure(new RuntimeException(s"cannot calculate total for order. sizegroups[$targetSizeGroups] found by size[${sizeAndCatalog.size}] are" +
                    s" not present in [${sizeAndCatalog}]"))
                }
//              }
//              case None => Failure(new RuntimeException(s"no sizegroup found for size ${sizeAndCatalog.size}"))
//            }
          })
        }
      }
      //      }
      //      result
    }

    private def calculateNumberOfPairs(order: JsObject): Try[Int] = {
      val qs = (order \ "items" \\ "quantity")
      Success(qs.map(_.as[Int]).sum)
    }

    def findSizeGroupIdBySize(sizeGroups: List[JsObject], size: Int): List[IdType] = {
      def sizeMatch(obj: JsObject, size: Int) = {
        val above = (obj \ "from").asOpt[Int].map(_ <= size).getOrElse(false)
        val below = (obj \ "to").asOpt[Int].map(_ >= size).getOrElse(false)
        above & below
      }
      sizeGroups.collect({ case g: JsObject if (sizeMatch(g, size)) => (g \ "_id").as[IdType]})
    }


  }

  override val orderPrintService = new OrderPrintServiceInternal with ReportFormats with DateFormatSupport {
    override def getPdf(orderId: IdType): Future[Try[Option[OrderReportContainer]]] = {
      val order = crudService.getById[OrderIn](orderId)



      order.map {
        _.map {
          _.flatMap {
            orderJson => {
              val deadlinesIds = (orderJson \ "deadlines").as[JsArray].value.map(v => (v \ "deadlineTypeId").as[String])
              val deadlineNames = getDeadlines(deadlinesIds)

              val agentId = (orderJson \ "originatorId").as[IdType]
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
                  } yield {
                    val report=binReport(mapOrderPrint(dd, aaa, ccc, orderJson))
                    OrderReportContainer(aaa,ccc,orderJson,report)
                  }


              Await.result(ff, 120 seconds) match {
                case Success(report) => report
                case Failure(e) => throw e
              }


            }

          }

        }
      }
    }

    def storePdf(req:OrderActionMessage)={
      getPdf(req.orderId).map {
        _.map {
          _.flatMap{
            (reportContainer) =>
              storeInternal(req.storageKey,new ByteArrayInputStream(reportContainer.bytes))
          }
        }
      }
    }

    def storeInternal(objectKey: String, stream: ByteArrayInputStream)={
      storage.storePdf(objectKey,stream)
    }

    private def mapOrderPrint(deadlineTypes: Map[IdType, String], agent: JsObject, customer: JsObject, order: JsObject): OrderReport = {
      def address(order: JsObject)(mode: String) = {

        val postalcode = (order \ mode \ "postalcode").asOpt[String].getOrElse("")
        val city = (order \ mode \ "city").asOpt[String].getOrElse("")
        val addressLine = (order \ mode \ "address").asOpt[String].getOrElse("")

        s"$postalcode $city $addressLine"
      }

      val addressFn = address(order) _
      val shippingName = (order \ "shippingAddress" \ "description").asOpt[String].getOrElse("")

      val customerName = (customer \ "name").asOpt[String].getOrElse("")
      val taxExemptNumber = (customer \ "taxExemptNumber").asOpt[String].getOrElse("")
      val bankAccount = (customer \ "bankAccountNumber").asOpt[String].getOrElse("")

      val customerReport = CustomerReport(customerName, addressFn("billingAddress"), shippingName, addressFn("shippingAddress"), taxExemptNumber, bankAccount)

      val agentLastName = (agent \ "lastName").asOpt[String].getOrElse("")
      val agentFirstName = (agent \ "firstName").asOpt[String].getOrElse("")
      val agentName = s"$agentFirstName $agentLastName"

      val phonenumber = (agent \ "phonenumbers").as[JsArray].value.map(v => (v \ "number").asOpt[String].getOrElse("")).headOption.getOrElse("")


      val email = (agent \ "emails").as[JsArray].value.map(v => (v \ "address").asOpt[String].getOrElse("")).headOption.getOrElse("")

      val agentReport = AgentReport(agentName, "", phonenumber, email = email)

      val sortimentTriples = (order \ "items").as[JsArray].value.map(p => {
        val itemNumber = (p \ "product" \ "itemNumber").as[String]
        val imageUrl = (p \ "product" \ "image").asOpt[String].getOrElse("")
        (itemNumber, imageUrl, SortimentItem((p \ "size").as[Int], (p \ "quantity").as[Int]))
      })

      val products = sortimentTriples.foldLeft(Map[String, (String, List[SortimentItem])]())((map, triple) => {
        map.get(triple._1) match {
          case Some(p) => {
            map ++ Map(triple._1 ->(triple._2, (triple._3 :: p._2)))
          }
          case None => map ++ Map(triple._1 ->(triple._2, List(triple._3)))
        }

      })

      val productlist = products.map(triple => ProductReport(triple._1, triple._2._1, triple._2._2)).toList

      val orderId = (order \ "_id").as[IdType]

      val orderNumber = (order \ "orderNumber").as[String]

      val deadlines = (order \ "deadlines").as[JsArray].value.map(json => {
        val deadline = (json \ "date").as[DateTime]
        val deadlinetypeid = (json \ "deadlineTypeId").as[String]
        val deadlineName = deadlineTypes(deadlinetypeid)
        (deadlineName, deadline)
      })

      val deadline1 = deadlines.lift(0)
      val deadline2 = deadlines.lift(1)
      val deadline3 = deadlines.lift(2)
      val deadline4 = deadlines.lift(3)
      val deadline5 = deadlines.lift(4)

      val total = (order \ "total").as[Int]

      val lastModified = (order \ "lastModifiedAt").as[DateTime]

      OrderReport(orderId, orderNumber, lastModified, deadline1, deadline2, deadline3, deadline4, deadline5, customerReport, agentReport, productlist, total)

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
      val query = DbQuery(Json.obj("$or" -> exprs))
      Logger.debug(s"deadlines query: $query")
      val ds = crudService.find[DeadlineTypeIn](query)
      ds.map {
        _.map {
          jsonList => {

            jsonList.foldLeft(Map[IdType, String]())(
              (map, json) => {
                Logger.debug(s"deadlines: $json")
                map ++ Map((json \ "_id").as[IdType] -> (json \ "name").as[String])
              }
            )


          }
        }
      }
    }


  }


}

trait OrderRepositoryInternal {
  def orderId(): Future[Try[Int]]

  def ensureIndexOnOrderId: Future[Unit]
}

trait OrderRepositoryComponent {
  val orderRepository: OrderRepositoryInternal
}

trait MongoOrderRepository extends OrderRepositoryComponent {
  this: Mongo =>
  override val orderRepository = new OrderRepositoryInternal {

    override def orderId() = {
      mongo.nextValue[OrderIn]("orderId")
    }

    override def ensureIndexOnOrderId = mongo.ensureUniqueIndex[OrderIn]("orderId").map((result) => ())
  }
}



