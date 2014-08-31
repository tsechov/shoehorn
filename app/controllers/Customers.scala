package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{CustomerIn, CustomerUpdate, CustomerCreate}
import controllers.utils.CrudController


object Customers extends CrudController {
  override type MODEL = CustomerIn
  override type UPDATEMODEL = CustomerUpdate
  override type CREATEMODEL = CustomerCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Customers.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}