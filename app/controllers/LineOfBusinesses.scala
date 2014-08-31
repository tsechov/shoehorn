package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{LineOfBusinessIn, LineOfBusinessUpdate, LineOfBusinessCreate}
import controllers.utils.CrudController


object LineOfBusinesses extends CrudController {
  override type MODEL = LineOfBusinessIn
  override type UPDATEMODEL = LineOfBusinessUpdate
  override type CREATEMODEL = LineOfBusinessCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.LineOfBusinesses.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}