package services.basket

import models.AssetSupport._
import models.DateFormatSupport
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.json.Json.obj
import services.DbQuery
import services.mongo.MongoCollection
import services.mongo.MongoRead
import services.mongo.MongoUpdate

import scala.concurrent.Future

/**
 * @author tsechov
 */

case class PricedSizeGroup2(sizeGroupId: IdType, unitPrice: Int)

case class CatalogSw2(
                       catalogId: IdType,
                       sizeGroups: List[PricedSizeGroup2]
                       )

case class ProductIn2(
                       _id: IdType,
                       createdAt: DateTime,
                       lastModifiedAt: DateTime,
                       active: Boolean,
                       description: String,
                       name: String,
                       itemNumber: String,

                       image: UrlType,
                       imageThumb: UrlType,

                       catalogs: List[CatalogSw2])

case class BasketItem2(product: ProductIn2, quantity: Int, size: Int)

case class BasketIn2(_id: IdType,

                     items: List[BasketItem2])

object BasketIn2 extends DateFormatSupport {
  implicit val pricedSizeGroup2Format = Json.format[PricedSizeGroup2]
  implicit val catalogSw2Format = Json.format[CatalogSw2]
  implicit val productInFormat = Json.format[ProductIn2]
  implicit val basketItemFormat = Json.format[BasketItem2]
  implicit val basketInFormat = Json.format[BasketIn2]
}

class BasketItemService extends MongoCollection with MongoRead with MongoUpdate {

  override def collectionName: String = "baskets"

  def updateItems(basketUpdate: BasketIn2): Future[Unit] = {
    val selector = obj(idFieldName -> basketUpdate._id)

    val result = for {
      basket <- findOne[BasketIn2](DbQuery(selector))
      newItems = updateItems(basketUpdate.items, basket.items)
      newBasket = basket.copy(items = newItems)
      json = Json.toJson(newBasket)
      result <- update(selector, json)

    } yield result
    result.flatMap {
      case lastError if lastError.ok => Future.successful()
      case lastError => Future.failed(new IllegalStateException(lastError))
    }
  }

  case class ProductKey(_id: IdType, size: Int)


  private def updateItems(updateItems: List[BasketItem2], targetItems: List[BasketItem2]) = {
    val res = (targetItems ::: updateItems).groupBy(toKey).reduce((aaa, aaa) => ???)
    targetItems.map {
      targetItem => {
        val matches = updateItems.filter(toKey(_) == toKey(targetItem))
        matches match {
          case Nil => targetItem
          case list => targetItem.copy(quantity = list.map(_.size).sum)
        }


      }
    }.filterNot(_.size == 0)
  }

  private def toKey(item: BasketItem2) = ProductKey(item.product._id, item.size)

}
