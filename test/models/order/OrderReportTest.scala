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
      val items1 = ProductReport("1011-22432", "https://raw.githubusercontent.com/tsechov/shoehorn/68ea2560e9bcc703703565bf728e5a3a93ececd5/test/resources/1195-21709-thumb.jpg", sortiment1, List(PriceItem(1, 2, 3), PriceItem(4, 5, 6)))
      val sortiment2 = List(SortimentItem(19, 2), SortimentItem(24, 1), SortimentItem(34, 5))
      val items2 = ProductReport("1127-22182", "https://raw.githubusercontent.com/tsechov/shoehorn/68ea2560e9bcc703703565bf728e5a3a93ececd5/test/resources/1222-43633-thumb.jpg", sortiment2, List(PriceItem(6, 7, 8), PriceItem(9, 10, 11)))
      val order = OrderReport("idjool", "ordernumber", new DateTime, Some(("d1", new DateTime)), Some(("d3", new DateTime)), None, None, None, customer, agent, List(items1, items2), 16)

      val xml = ToXml.get(order)

      println(xml)

      success
    }
  }

}
