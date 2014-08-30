package models.customer

import play.api.libs.json.Json
import models._
import models.AssetSupport._
import org.joda.time.DateTime

case class CompanyTypeIn(_id: IdType,
                         createdAt: DateTime,
                         lastModifiedAt: DateTime,
                         active: Boolean,
                         description: String,
                         name: String) extends AssetIn with AssetUpdateBuilder[CompanyTypeUpdate] {
  override def fillup(lastModifiedAt: DateTime): CompanyTypeUpdate = CompanyTypeUpdate(lastModifiedAt, active, description, name)
}

object CompanyTypeIn extends AssetInCompanion[CompanyTypeIn] {
  val collectionName = "companytypes"
  val format = Json.format[CompanyTypeIn]
}


case class CompanyTypeUpdate(lastModifiedAt: DateTime,
                             active: Boolean,
                             description: String,
                             name: String) extends AssetUpdate

object CompanyTypeUpdate extends AssetUpdateCompanion[CompanyTypeUpdate] {
  val format = Json.format[CompanyTypeUpdate]
  val collectionName = CompanyTypeIn.collectionName


}

case class CompanyTypeCreate(active: Boolean,
                             description: String,
                             name: String) extends AssetCreate[CompanyTypeIn] {
  override def fillup(b: AssetBase) = CompanyTypeIn(b.id, b.createdAt, b.lastModifiedAt, active, description, name)
}

object CompanyTypeCreate {
  implicit val reads = Json.reads[CompanyTypeCreate]
}


