package controllers

import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.customer.{AgentIn, AgentUpdate, AgentCreate}


object Agents extends CrudController {
  override type MODEL = AgentIn
  override type UPDATEMODEL = AgentUpdate
  override type CREATEMODEL = AgentCreate

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Agents.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String) = Action.async {
    super.delete(id)
  }

}