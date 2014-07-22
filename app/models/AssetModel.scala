package models

import org.joda.time.DateTime

trait AssetModel {
  def id: Option[String]

  def createdAt: DateTime

  def lastModifiedAt: DateTime

  def active: Boolean

  def description: String


}

trait AssetModelSupport {

  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  type IdType = String

  val idFieldName = "_id"
  val createdAtFieldName = "createdAt"
  val lastModifiedAtFieldName = "lastModifiedAt"

  val idPath = (__ \ idFieldName)
  val createdAtPath = (__ \ createdAtFieldName)
  val lastModifiedAtPath = (__ \ lastModifiedAtFieldName)
  private val active = (__ \ "active")
  private val description = (__ \ "description")


  val pattern = "yyyy-MM-dd'T'HH:mm:ssZ"
  implicit val dateFormat = Format[DateTime](Reads.jodaDateReads(pattern), Writes.jodaDateWrites(pattern))

  val readsBuilder =
    idPath.readNullable[IdType] and
      createdAtPath.read[DateTime] and
      lastModifiedAtPath.read[DateTime] and
      active.read[Boolean] and
      description.read[String]


  val writesBuilder =
    idPath.writeNullable[IdType] and
      createdAtPath.write[DateTime] and
      lastModifiedAtPath.write[DateTime] and
      active.write[Boolean] and
      description.write[String]

  /**
   *
   * @param json
   * @return
   */
  def createTransformer(json: JsValue): (IdType,DateTime) => Reads[JsObject] = { (id,date) => {
    (__).json.update(__.read[JsObject].map { root =>
      root ++ Json.obj(idFieldName -> id,createdAtFieldName -> date, lastModifiedAtFieldName -> date)
    })
  }
  }

}

object AssetModelSupport extends AssetModelSupport
