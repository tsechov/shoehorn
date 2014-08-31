package models.common

import models.AssetSupport
import play.api.libs.json.{Format, Writes, Reads}

case class ReferenceTo[A](value: AssetSupport.IdType)

object ReferenceTo {
  val r = Reads.StringReads
  val w = Writes.StringWrites
  implicit val format = Format(r, w)

}
