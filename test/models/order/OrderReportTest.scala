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
      val sortiment1 = List(SortimentItem(18, 2), SortimentItem(24, 1), SortimentItem(34, 5))
      val items1 = ProductReport("1011-22432", "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1011-22432-full.jpg", sortiment1)
      val sortiment2 = List(SortimentItem(19, 2), SortimentItem(24, 1), SortimentItem(34, 5))
      val items2 = ProductReport("1127-22182", "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1127-22182-full.jpg", sortiment2)
      val order = OrderReport("idjool", "ordernumber", new DateTime, new DateTime, new DateTime, customer, agent, List(items1, items2), 16)

      val xml = ToXml.get(order)

      println(xml)

      success
    }
  }

}
