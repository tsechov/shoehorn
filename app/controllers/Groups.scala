package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{GroupIn, GroupUpdate, GroupCreate}
import controllers.utils.CrudController


object Groups extends CrudController {
  override type MODEL = GroupIn
  override type UPDATEMODEL = GroupUpdate
  override type CREATEMODEL = GroupCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Groups.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}