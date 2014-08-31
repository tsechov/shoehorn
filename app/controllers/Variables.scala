package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import controllers.utils.CrudController
import models.order.{VariableCreate, VariableUpdate, VariableIn}


object Variables extends CrudController {
  override type MODEL = VariableIn
  override type UPDATEMODEL = VariableUpdate
  override type CREATEMODEL = VariableCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Variables.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}