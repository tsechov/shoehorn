package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType
import models.Product
import models.ProductCreate
import models.ProductUpdate
import play.api.mvc.BodyParsers.parse


object Products extends CrudController {
  override type MODEL = Product
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
