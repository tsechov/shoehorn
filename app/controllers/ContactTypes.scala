package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{ContactTypeIn, ContactTypeUpdate, ContactTypeCreate}
import controllers.utils.CrudController


object ContactTypes extends CrudController {
  override type MODEL = ContactTypeIn
  override type UPDATEMODEL = ContactTypeUpdate
  override type CREATEMODEL = ContactTypeCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.ContactTypes.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}