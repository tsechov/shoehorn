package models.customer

import models.common.{District, Contact}
import play.api.libs.json.Json

case class Agent(
                  warehouse: Warehouse,
                  districts: List[District],
                  contact: Contact)

object Agent {
  implicit val format = Json.format[Agent]
}
