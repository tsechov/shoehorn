package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime


case class CatalogCreate(
                          active: Boolean,
                          description: String,
                          year: Int,
                          season: String,
                          status: Boolean,
                          webStatus: Boolean
                          )

object CatalogCreate extends CatalogPaths{
  implicit val reads: Reads[CatalogCreate] =
    (activePath.read[Boolean] and
      descriptionPath.read[String] and
      yearPath.read[Int] and
      seasonPath.read[String] and
      statusPath.read[Boolean] and
      webStatusPath.read[Boolean])(CatalogCreate.apply _)



  def transformer(json: JsValue): (Catalog.IdType, DateTime) => Reads[JsObject] = {
    (id, date) => {
      (__).json.update(__.read[JsObject].map {
        root =>
          root ++ Json.obj(CatalogSupport.idFieldName -> id, "createdAt" -> date, "lastModifiedAt" -> date)
      })
    }
  }
}


case class CatalogUpdate(
                          _id: Catalog.IdType,
                          active: Boolean,
                          description: String,
                          year: Int,
                          season: String,
                          status: Boolean,
                          webStatus: Boolean
                          )

object CatalogUpdate extends CatalogPaths{
  implicit val reads: Reads[CatalogUpdate] =
    {

      (idPath.read[Catalog.IdType] and
        activePath.read[Boolean] and
        descriptionPath.read[String] and
        yearPath.read[Int] and
        seasonPath.read[String] and
        statusPath.read[Boolean] and
        webStatusPath.read[Boolean])(CatalogUpdate.apply _)
    }
}

case class Catalog(
                    id: Catalog.IdType,
                    createdAt: DateTime,
                    lastModifiedAt: DateTime,
                    active: Boolean,
                    description: String,
                    year: Int,
                    season: String,
                    status: Boolean,
                    webStatus: Boolean
                    )

object Catalog extends CatalogPaths with DateFormatSupport{
  val reads:Reads[Catalog] =  {
    (idPath.read[Catalog.IdType] and
      createdAtPath.read[DateTime] and
      lastModifiedAtPath.read[DateTime] and
      activePath.read[Boolean] and
      descriptionPath.read[String] and
      yearPath.read[Int] and
      seasonPath.read[String] and
      statusPath.read[Boolean] and
      webStatusPath.read[Boolean])(Catalog.apply _)
  }

  val writes:Writes[Catalog] = {
    (idPath.write[Catalog.IdType] and
      createdAtPath.write[DateTime] and
      lastModifiedAtPath.write[DateTime] and
      activePath.write[Boolean] and
      descriptionPath.write[String] and
      yearPath.write[Int] and
      seasonPath.write[String] and
      statusPath.write[Boolean] and
      webStatusPath.write[Boolean])(unlift(Catalog.unapply _))
  }

  implicit val catalogFormats:Format[Catalog] = Format(reads,writes)
}

trait CatalogPaths{
  val idPath= __ \ CatalogSupport.idFieldName
  val createdAtPath= __ \ "createdAt"
  val lastModifiedAtPath= __ \ "lastModifiedAt"
  val activePath= __ \ "active"
  val descriptionPath= __ \ "description"
  val yearPath= __ \ "year"
  val seasonPath= __ \ "season"
  val statusPath = __ \ "status"
  val webStatusPath = __ \ "webStatus"
  type IdType = String
}

object CatalogSupport {
  val idFieldName="_id"
}




