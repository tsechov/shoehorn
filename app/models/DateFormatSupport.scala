package models

import play.api.libs.json.{Writes, Reads, Format}
import org.joda.time.DateTime


trait DateFormatSupport {
  val pattern = "yyyy-MM-dd'T'HH:mm:ssZ"
  implicit val dateFormat = Format[DateTime](Reads.jodaDateReads(pattern), Writes.jodaDateWrites(pattern))

}
