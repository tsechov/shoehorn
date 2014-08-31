package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{EmailTypeIn, EmailTypeUpdate, EmailTypeCreate}
import controllers.utils.CrudController


object EmailTypes extends CrudController {
  override type MODEL = EmailTypeIn
  override type UPDATEMODEL = EmailTypeUpdate
  override type CREATEMODEL = EmailTypeCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.EmailTypes.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}