package models


import org.joda.time.DateTime
case class CreateCatalog(
                    active: Boolean,
                    description: String,
                    year: Int,
                    season: String,
                    status: Boolean,
                    webStatus: Boolean
                    ) extends CreateAssetModel


case class Catalog(
                    id: Option[Catalog.IdType],
                    createdAt: DateTime,
                    lastModifiedAt: DateTime,
                    active: Boolean,
                    description: String,
                    year: Int,
                    season: String,
                    status: Boolean,
                    webStatus: Boolean
                    ) extends AssetModel

object Catalog extends AssetModelSupport {

  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  private val year = (__ \ "year")
  private val season = (__ \ "season")
  private val status = (__ \ "status")
  private val webStatus = (__ \ "webStatus")

  val catalogReads: Reads[Catalog] = (
    readsBuilder and
      year.read[Int] and
      season.read[String] and
      status.read[Boolean] and
      webStatus.read[Boolean]
    )(Catalog.apply _)

  val catalogWrites: Writes[Catalog] = (
    writesBuilder and
      year.write[Int] and
      season.write[String] and
      status.write[Boolean] and
      webStatus.write[Boolean]
    )(unlift(Catalog.unapply))

  implicit val catalogFormats = Format[Catalog](catalogReads, catalogWrites)


}
