package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{WarehouseCreate, WarehouseUpdate, WarehouseIn}
import controllers.utils.CrudController


object Warehouses extends CrudController {
  override type MODEL = WarehouseIn
  override type UPDATEMODEL = WarehouseUpdate
  override type CREATEMODEL = WarehouseCreate


  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Warehouses.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}