package services.reporting

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import akka.testkit._
import org.joda.time.DateTime
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpecLike, BeforeAndAfterAll, WordSpec}

import play.api.test.{FakeApplication, PlaySpecification}
import services.mailer.{Mail, OrderCreate, MailerActor}




class ReportControllerTest extends TestKit(ActorSystem("ReportControllerTest",
  ConfigFactory.parseString(ReportControllerTest.config)))
with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {


  "ReportController actor" should {
    "act on report request" in {
      within(1 second) {
        import akka.pattern.ask
        val reporter = system.actorOf(Props[ReportController])
        val reqId=UUID.randomUUID
        reporter ! OrderReportRequest(reqId,"blah",new DateTime)
        reporter ? OrderReportQuery(reqId)
        expectNoMsg


      }

    }
  }
}

object ReportControllerTest {
  val config = """
    akka {
      loglevel = "DEBUG"
    }
               """
}
