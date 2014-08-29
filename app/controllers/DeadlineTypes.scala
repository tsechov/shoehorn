package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.order.{DeadlineTypeIn, DeadlineTypeUpdate, DeadlineTypeCreate}


object DeadlineTypes extends CrudController {
  override type MODEL = DeadlineTypeIn
  override type UPDATEMODEL = DeadlineTypeUpdate
  override type CREATEMODEL = DeadlineTypeCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.DeadlineTypes.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}