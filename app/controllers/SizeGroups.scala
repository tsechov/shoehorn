package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.product.{SizeGroupCreate, SizeGroupUpdate, SizeGroupIn}


object SizeGroups extends CrudController {
  override type MODEL = SizeGroupIn
  override type UPDATEMODEL = SizeGroupUpdate
  override type CREATEMODEL = SizeGroupCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.SizeGroups.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}