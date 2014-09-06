package models.order

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import services.reporting.ToXml


class OrderReportTest extends Specification {


  "OrderReport" should {

    "should be writeable into xml" in {
      val address = "address jool"
      val shippingName = "shipping name"
      val shippingAddress = "shipping address"
      val customer = CustomerReport("name", address, shippingName, shippingAddress, "adoszam123", "bankszamlaszamjool")
      val agent = AgentReport("neve", "payment", "phonenumber123", email = "foo@bar.com")
      val sortiment1 = List(SortimentItem(18, 2), SortimentItem(24, 1), SortimentItem(34, 5))
      val items1 = ProductReport("1011-22432", "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1011-22432-full.jpg", sortiment1)
      val sortiment2 = List(SortimentItem(19, 2), SortimentItem(24, 1), SortimentItem(34, 5))
      val items2 = ProductReport("1127-22182", "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1127-22182-full.jpg", sortiment2)
      val order = OrderReport("idjool", "ordernumber", new DateTime, Some(("d1", new DateTime)), Some(("d3", new DateTime)), None, None, None, customer, agent, List(items1, items2), 16)

      val xml = ToXml.get(order)

      println(xml)

      success
    }
  }

}
