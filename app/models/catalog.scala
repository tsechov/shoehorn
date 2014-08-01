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

object CatalogCreate extends CatalogPaths {
  implicit val reads: Reads[CatalogCreate] =
    (activePath.read[Boolean] and
      descriptionPath.read[String] and
      yearPath.read[Int] and
      seasonPath.read[String] and
      statusPath.read[Boolean] and
      webStatusPath.read[Boolean])(CatalogCreate.apply _)


}


case class CatalogUpdate(
                          lastModifiedAt: DateTime,
                          active: Boolean,
                          description: String,
                          year: Int,
                          season: String,
                          status: Boolean,
                          webStatus: Boolean
                          )

object CatalogUpdate extends CatalogPaths with DateFormatSupport {
  implicit val reads: Reads[CatalogUpdate] = {

    (lastModifiedAtPath.read[DateTime] and
      activePath.read[Boolean] and
      descriptionPath.read[String] and
      yearPath.read[Int] and
      seasonPath.read[String] and
      statusPath.read[Boolean] and
      webStatusPath.read[Boolean])(CatalogUpdate.apply _)
  }


}

case class Catalog(
                    id: AssetSupport.IdType,
                    createdAt: DateTime,
                    lastModifiedAt: DateTime,
                    active: Boolean,
                    description: String,
                    year: Int,
                    season: String,
                    status: Boolean,
                    webStatus: Boolean
                    )

object Catalog extends CatalogPaths with DateFormatSupport {

  val reads: Reads[Catalog] = {
    (idPath.read[AssetSupport.IdType] and
      createdAtPath.read[DateTime] and
      lastModifiedAtPath.read[DateTime] and
      activePath.read[Boolean] and
      descriptionPath.read[String] and
      yearPath.read[Int] and
      seasonPath.read[String] and
      statusPath.read[Boolean] and
      webStatusPath.read[Boolean]).apply(Catalog.apply _)
  }

  val writes: Writes[Catalog] = {
    (idPath.write[AssetSupport.IdType] and
      createdAtPath.write[DateTime] and
      lastModifiedAtPath.write[DateTime] and
      activePath.write[Boolean] and
      descriptionPath.write[String] and
      yearPath.write[Int] and
      seasonPath.write[String] and
      statusPath.write[Boolean] and
      webStatusPath.write[Boolean])(unlift(Catalog.unapply _))
  }

  implicit val catalogFormats = Format(reads, writes)
}

trait CatalogPaths extends AssetPaths {

  val yearPath = __ \ "year"
  val seasonPath = __ \ "season"
  val statusPath = __ \ "status"
  val webStatusPath = __ \ "webStatus"

}






