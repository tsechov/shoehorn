package services.reporting

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import akka.testkit._
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import services.{OrderReportRequest, OrderPrintServiceInternal}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpecLike, BeforeAndAfterAll, WordSpec}

import play.api.test.{FakeApplication, PlaySpecification}
import services.mailer.{Mail, OrderCreate, MailSender}




class ReportGeneratorTest extends TestKit(ActorSystem("ReportGeneratorTest",
  ConfigFactory.parseString(ReportGeneratorTest.config)))
with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with Mockito{


  "ReportGenerator actor" should {
    "act on report request" in {
      within(1 second) {

        val mockPrinter=mock[OrderPrintServiceInternal]
        val reporter = system.actorOf(Props(classOf[ReportGenerator],mockPrinter))
        val reqId=UUID.randomUUID
        val req=OrderReportRequest(reqId,"blah","foo")
        reporter ! req
        expectNoMsg
        org.mockito.Mockito.verify(mockPrinter).storePdf(req)


      }

    }
  }
}

object ReportGeneratorTest {
  val config = """
    akka {
      loglevel = "DEBUG"
    }
               """
}
