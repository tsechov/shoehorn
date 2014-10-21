package controllers

import play.api.mvc.{SimpleResult, Action}
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.order.{OrderIn, OrderUpdate, OrderCreate}
import controllers.utils.CrudController
import play.api.libs.json._
import services.{DbQuery, runtime, JsonErrors}
import play.api.http.{HeaderNames, ContentTypes}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.{Try, Failure, Success}
import play.api.Logger
import scala.concurrent.Future


object Orders extends CrudController {

  lazy val orderService = runtime orderService

  orderService.ensureIndexOnOrderId

  override type MODEL = OrderIn
  override type UPDATEMODEL = OrderUpdate
  override type CREATEMODEL = OrderCreate

  private def jsError[A]: PartialFunction[Try[A], SimpleResult] = {
    case Failure(error: JsonErrors) => {
      val jsonError = JsError.toFlatJson(JsError(error.errors))
      Logger.debug(s"invalid ordercreate: $jsonError")
      BadRequest(jsonError)
    }
  }

  def create = Action.async(parse.json) {
    request =>
      val input = request.body.as[JsObject]
      Logger.debug("order in for create: " + Json.prettyPrint(input))
      orderService.createOrder(input).map {
        jsError[IdType] orElse internalServerError[IdType]("failed to create order") orElse {
          case Success(id) => Created.as(ContentTypes.JSON)
            .withHeaders(HeaderNames.LOCATION -> locationUrl(id, id => controllers.routes.Orders.getById(id)))
            .withHeaders(filters.RESOURCE_ID_HEADER -> id)
        }
      }.recover {
        case error => {
          Logger.error("cant create order", error)
          InternalServerError
        }
      }
  }

  def update(id: IdType) = Action.async(parse.json) {
    request => {
      val input = request.body.as[JsObject]
      Logger.debug("order in for update: " + Json.prettyPrint(input))
      orderService.updateOrder(id, input).map {
        jsError[Unit] orElse internalServerError[Unit]("failed to update order") orElse {
          case Success(_) => Ok
        }
      }.recover {
        case error => {
          Logger.error("cant update order", error)
          InternalServerError
        }
      }
    }

  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }


  def list = Action.async {
    val queryJson = Json.obj()
    service.find[MODEL](DbQuery(queryJson, Some(Json.obj("items" -> 0)))).map(listResult[JsObject]("orderlist"))
  }

}