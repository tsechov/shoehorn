package models

import play.api.libs.json.{Writes, Reads, Format}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat


trait DateFormatSupport {


  implicit val dateFormat = Format[DateTime](Reads.jodaDateReads(DateFormatSupport.pattern), Writes.jodaDateWrites(DateFormatSupport.pattern))

}

object DateFormatSupport {
  val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
  val fmt = DateTimeFormat.forPattern(pattern)
}


