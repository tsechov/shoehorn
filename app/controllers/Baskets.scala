package controllers

import java.util.UUID

import org.joda.time.DateTime
import org.scalactic.Fail
import play.api.Logger
import play.api.mvc.Action
import models.AssetSupport.IdType

import play.api.mvc.BodyParsers.parse
import models.order.{BasketItem, BasketIn, BasketUpdate, BasketCreate}
import controllers.utils.CrudController
import play.api.mvc.SimpleResult
import services.basket.BasketIn2
import services.basket.BasketItemService
import scala.concurrent.Future
import play.api.libs.json._
import scala.util.Failure
import scala.util.Success
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.util.Try


object Baskets extends CrudController {
  override type MODEL = BasketIn
  override type UPDATEMODEL = BasketUpdate
  override type CREATEMODEL = BasketCreate

  val basketItemService: BasketItemService = new BasketItemService

  def updateItems = Action.async(parse.json) {
    request => {

      val operation: PartialFunction[JsResult[BasketIn2], Future[SimpleResult]] = {
        badJsonRequest[BasketIn2]("erroneous input for basketitemupdate") orElse {
          case jsSuccess: JsSuccess[BasketIn2] => {
            val futureResult = basketItemService.updateItems(jsSuccess.get)
            futureResult.onFailure({
              case notFound: NoSuchElementException => NotFound
              case error => {
                val uuid = UUID.randomUUID().toString
                val msg = "cannot update basket items"
                Logger.error(s"[$uuid] - $msg: $error", error)
                InternalServerError(s"logId: [$uuid]")
              }
            })

            futureResult.map({ case _ => Ok})


          }
        }
      }
      operation(request.body.validate[BasketIn2])
    }

  }

  private def simpleInternalServerError[A](msg: String): PartialFunction[A, SimpleResult] = {
    case Failure(error) => {
      val uuid = UUID.randomUUID().toString
      Logger.error(s"[$uuid] - $msg: $error", error)
      InternalServerError(s"logId: [$uuid]")
    }
  }

  def create = Action.async(parse.json) {
    request =>
      super.create(request.body, id => controllers.routes.Baskets.getById(id))
  }

  def update(id: IdType) = Action.async(parse.json) {
    request =>
      super.update(id, request.body)
  }

  def delete(id: String, itemNumber: Option[String], size: Option[Int]) = Action.async {
    itemNumber match {
      case None => size match {
        case None => super.delete(id)
        case _ => Future.successful(BadRequest) //size is not allowed without itemnumber
      }
      case Some(itemNumberValue) => {
        val filter: BasketItem => Boolean = (item) =>
          size match {
            //both exist do deleteByItemNumberAndSize
            case Some(sizeValue) => (item.product.itemNumber == itemNumberValue && item.size == sizeValue)
            //only itemNumber exists do deleteByItemNumber
            case None => (item.product.itemNumber == itemNumberValue)
          }


        //val entity: Future[Try[Option[JsObject]]] = service.getById[MODEL](id)


        val res = service.getById[MODEL](id).map {
          internalServerError[Option[JsObject]]("[getById] error") orElse {
            case Success(result) => result match {
              case Some(entity) => {
                Json.fromJson[BasketIn](entity) match {
                  case JsSuccess(basket, _) => {
                    val newItems = basket.items.filterNot(filter)
                    val udpated = basket.copy(items = newItems)
                    val res = service.update[MODEL, UPDATEMODEL](id)(udpated).map {

                      internalServerError[Unit]("[basket update] error") orElse {
                        case Success(_) => Ok
                      }
                    }
                    //FIXME: properly handle nested service operations
                    Ok

                  }
                  case JsError(_) => InternalServerError
                }

              }
              case None => NotFound
            }
          }
        }

        res
      }


    }


  }


}