package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType
import play.api.mvc.BodyParsers.parse
import models.product.{ProductIn, ProductUpdate, ProductCreate}
import controllers.utils.CrudController


object Products extends CrudController {
  override type MODEL = ProductIn
  override type UPDATEMODEL = ProductUpdate
  override type CREATEMODEL = ProductCreate


  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Products.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}
