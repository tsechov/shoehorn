package models.order

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime
import models.product._
import models.AssetBase

case class BasketItem(product: ProductIn, quantity: Int, size: Int)

object BasketItem {
  implicit val format = Json.format[BasketItem]
}

case class BasketIn(_id: IdType,
                    createdAt: DateTime,
                    lastModifiedAt: DateTime,
                    active: Boolean,
                    description: String,
                    items: List[BasketItem]) extends AssetIn with AssetUpdateBuilder[BasketUpdate] {
  override def fillup(lastModifiedAt: DateTime): BasketUpdate = BasketUpdate(lastModifiedAt, active, description, items)
}

object BasketIn extends AssetInCompanion[BasketIn] {
  val collectionName = "baskets"
  val format = Json.format[BasketIn]
}


case class BasketUpdate(lastModifiedAt: DateTime,
                        active: Boolean,
                        description: String,
                        items: List[BasketItem]) extends AssetUpdate

object BasketUpdate extends AssetUpdateCompanion[BasketUpdate] {
  val format = Json.format[BasketUpdate]
  val collectionName = BasketIn.collectionName


}

case class BasketCreate(active: Boolean,
                        description: String,
                        items: List[BasketItem]) extends AssetCreate[BasketIn] {
  override def fillup(b: AssetBase) = BasketIn(b.id, b.createdAt, b.lastModifiedAt, active, description, items)
}

object BasketCreate {
  implicit val reads = Json.reads[BasketCreate]
}


