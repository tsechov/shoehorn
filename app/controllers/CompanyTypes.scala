package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{CompanyTypeIn, CompanyTypeUpdate, CompanyTypeCreate}


object CompanyTypes extends CrudController {
  override type MODEL = CompanyTypeIn
  override type UPDATEMODEL = CompanyTypeUpdate
  override type CREATEMODEL = CompanyTypeCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.CompanyTypes.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}