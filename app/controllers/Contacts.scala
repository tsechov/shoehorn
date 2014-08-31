package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{ContactIn, ContactUpdate, ContactCreate}
import controllers.utils.CrudController


object Contacts extends CrudController {
  override type MODEL = ContactIn
  override type UPDATEMODEL = ContactUpdate
  override type CREATEMODEL = ContactCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Contacts.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}