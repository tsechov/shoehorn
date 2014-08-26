package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{ShopCreate, ShopUpdate, ShopIn}


object Shops extends CrudController {
  override type MODEL = ShopIn
  override type UPDATEMODEL = ShopUpdate
  override type CREATEMODEL = ShopCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Shops.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}