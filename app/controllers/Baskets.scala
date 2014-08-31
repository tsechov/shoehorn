package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.order.{BasketIn, BasketUpdate, BasketCreate}
import controllers.utils.CrudController
import scala.concurrent.Future
import play.mvc.Results.Todo


object Baskets extends CrudController {
  override type MODEL = BasketIn
  override type UPDATEMODEL = BasketUpdate
  override type CREATEMODEL = BasketCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Baskets.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

  def deleteByItemNumber(id: String, itemNumber: String) = Action.async {
    Future.successful(new Todo().getWrappedSimpleResult)
  }

  def deleteByItemNumberAndSize(id: String, itemNumber: String, size: Int) = Action.async {
    Future.successful(Ok)
  }

}