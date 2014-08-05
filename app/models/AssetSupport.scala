package models

import play.api.libs.json._
import org.joda.time.DateTime

object AssetSupport {

  val idFieldName = "_id"
  val createdAtFieldName = "createdAt"
  val lastModifiedAtFieldName = "lastModifiedAt"
  val activeFieldName = "active"
  val descriptionFieldName = "description"

  type IdType = String



}

trait AssetPaths {

  import AssetSupport._

  val idPath = __ \ idFieldName
  val createdAtPath = __ \ createdAtFieldName
  val lastModifiedAtPath = __ \ lastModifiedAtFieldName
  val activePath = __ \ activeFieldName
  val descriptionPath = __ \ descriptionFieldName
}

object AssetTransform extends AssetPaths{

  def create(json: JsValue): (AssetSupport.IdType, DateTime) => Reads[JsObject] = {
    (id, date) => {
      import AssetSupport._
      (__).json.update(__.read[JsObject].map {
        root => root ++ Json.obj(idFieldName -> id, createdAtFieldName -> date, lastModifiedAtFieldName -> date)
      })
    }
  }

  def update(json: JsValue): (DateTime) => Reads[JsObject] = {
    (date) => {
      addDate(date) andThen createdAtPath.json.prune andThen idPath.json.prune
    }


  }

  private def addDate(date: DateTime) = {
    (__).json.update(__.read[JsObject].map {
      root => root ++ Json.obj(AssetSupport.lastModifiedAtFieldName -> date)
    })
  }
}

trait AssetCreate[A] {
  def fillup(id:AssetSupport.IdType,createdAt:DateTime,lastModifiedAt:DateTime):A
}

trait AssetUpdate[U] {
  def fillup(lastModifiedAt:DateTime):U
}
