package models.order

import org.specs2.mutable.Specification
import models.common.Address
import org.joda.time.DateTime
import services.reporting.ToXml


class OrderReportTest extends Specification {


  "OrderReport" should {

    "should be writeable into xml" in {
      val address = Address("typeid", "country", "district", "1134", "budapest", "address", "description jool")
      val shippingAddress = Address("typeid", "country", "district", "1134", "budapest", "shipping address", "shippin description jool")
      val customer = CustomerReport("name", address, shippingAddress, "adoszam123", "bankszamlaszamjool")
      val agent = AgentReport("neve", "payment", "phonenumber123", email = "foo@bar.com")
      val sortiment1 = List(SortimentItem(22, 2), SortimentItem(24, 1), SortimentItem(34, 5))
      val items1 = ProductReport("modelnumber", "color", sortiment1, 776, new DateTime)
      val sortiment2 = List(SortimentItem(22, 2), SortimentItem(24, 1), SortimentItem(34, 5))
      val items2 = ProductReport("modelnumber", "color", sortiment2, 776, new DateTime)
      val order = OrderReport("idjool", "ordernumber", new DateTime, customer, agent, List(items1, items2))

      val xml = ToXml.get(order)

      println(xml)

      success
    }
  }

}
