package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{PhoneNumberTypeIn, PhoneNumberTypeUpdate, PhoneNumberTypeCreate}
import controllers.utils.CrudController


object PhoneNumberTypes extends CrudController {
  override type MODEL = PhoneNumberTypeIn
  override type UPDATEMODEL = PhoneNumberTypeUpdate
  override type CREATEMODEL = PhoneNumberTypeCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.PhoneNumberTypes.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}