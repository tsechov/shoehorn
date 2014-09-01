package models

import play.api.libs.json._
import org.joda.time.DateTime
import models.AssetSupport.IdType
import services.CollectionName
import models.common.{Referable, ReferenceTo}

object AssetSupport {

  val idFieldName = "_id"
  val createdAtFieldName = "createdAt"
  val lastModifiedAtFieldName = "lastModifiedAt"
  val activeFieldName = "active"
  val descriptionFieldName = "description"

  type IdType = String
  type UrlType = String
  type RefType[A] = ReferenceTo[A]
  type OrderNumber = Int


}

trait AssetCreate[A] {

  def fillup(b: AssetBase): A
}

case class AssetBase(id: IdType, createdAt: DateTime, lastModifiedAt: DateTime)


trait AssetUpdateBuilder[U] {
  def fillup(lastModifiedAt: DateTime): U
}

trait AssetIn extends AssetUpdate {

  def _id: IdType

  def createdAt: DateTime

}

abstract class AssetInCompanion[A <: AssetIn] extends AssetUpdateCompanion[A] {

}


trait AssetUpdate {

  def lastModifiedAt: DateTime

  def active: Boolean

  def description: String
}


abstract class AssetUpdateCompanion[A <: AssetUpdate] extends DateFormatSupport {

  def collectionName: String

  implicit def format: Format[A]

  implicit def cn: CollectionName[A] = new CollectionName[A] {
    override def get: String = collectionName
  }


}


