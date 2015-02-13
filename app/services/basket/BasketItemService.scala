package services.basket

import models.AssetSupport._
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.json.Json.obj
import services.DbQuery
import services.mongo.MongoCollection
import services.mongo.MongoRead
import services.mongo.MongoUpdate

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

object BasketIn2 {
  implicit val pricedSizeGroup2Format = Json.format[PricedSizeGroup2]
  implicit val catalogSw2Format = Json.format[CatalogSw2]
  implicit val productInFormat = Json.format[ProductIn2]
  implicit val basketItemFormat = Json.format[BasketItem2]
  implicit val basketInFormat = Json.format[BasketIn2]
}

class BasketItemService extends MongoCollection with MongoRead with MongoUpdate {

  override def collectionName: String = "baskets"

  def update(basketUpdate: BasketIn2) = {
    val query = obj(idFieldName -> basketUpdate._id)

    for {
      basket <- findOne[BasketIn2](DbQuery(query))

    } yield basket
  }
}
