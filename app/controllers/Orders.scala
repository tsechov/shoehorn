package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.order.{OrderIn, OrderUpdate, OrderCreate}
import controllers.utils.CrudController
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.UUID
import scala.concurrent.Future
import play.api.Logger
import services.production


object Orders extends CrudController {

  lazy val orderService = production orderService

  orderService.ensureIndexOnOrderNumber

  override type MODEL = OrderIn
  override type UPDATEMODEL = OrderUpdate
  override type CREATEMODEL = OrderCreate

  def create = Action.async(parse.json) {
    request =>
      val orderNumber = orderService.orderNumber

      val transformer = (__).json.update(
        __.read[JsObject].map { o => o ++ Json.obj("orderNumber" -> orderNumber)}
      )

      val res = request.body.transform(transformer).map {

        super.create(_, id => controllers.routes.Orders.getById(id))
      }.recoverTotal {
        (e) => {
          Logger.error(s"failed to inject orderNumber: $e")
          Future.successful(InternalServerError)
        }
      }
      res
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}