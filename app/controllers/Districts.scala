package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{DistrictIn, DistrictUpdate, DistrictCreate}
import controllers.utils.CrudController


object Districts extends CrudController {
  override type MODEL = DistrictIn
  override type UPDATEMODEL = DistrictUpdate
  override type CREATEMODEL = DistrictCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Districts.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}