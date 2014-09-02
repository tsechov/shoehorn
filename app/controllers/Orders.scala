package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.order.{OrderIn, OrderUpdate, OrderCreate}
import controllers.utils.CrudController
import play.api.libs.json._
import services.production
import play.api.http.{HeaderNames, ContentTypes}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.Success
import play.api.Logger


object Orders extends CrudController {

  lazy val orderService = production orderService

  orderService.ensureIndexOnOrderId

  override type MODEL = OrderIn
  override type UPDATEMODEL = OrderUpdate
  override type CREATEMODEL = OrderCreate

  def create = Action.async(parse.json) {
    request =>
      val input = request.body.as[JsObject]
      Logger.debug("order in: " + Json.prettyPrint(input))
      orderService.createOrder(input).map {

        internalServerError[IdType]("failed to create order") orElse {
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
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}