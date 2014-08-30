package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{AddressTypeIn, AddressTypeUpdate, AddressTypeCreate}
import controllers.utils.CrudController


object AddressTypes extends CrudController {
  override type MODEL = AddressTypeIn
  override type UPDATEMODEL = AddressTypeUpdate
  override type CREATEMODEL = AddressTypeCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.AddressTypes.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}