package services.mailer

import org.specs2.mutable.Specification

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.testkit.TestActorRef
import play.api.libs.json.Json
import play.api.test.{PlaySpecification, FakeApplication}
import javax.net.ssl.{X509TrustManager, TrustManager, SSLContext}
import java.security.cert.X509Certificate

class MailerTest extends PlaySpecification {

  object TrustAll extends X509TrustManager {
    override def checkClientTrusted(p1: Array[X509Certificate], p2: String) = Unit

    override def getAcceptedIssuers = null

    override def checkServerTrusted(p1: Array[X509Certificate], p2: String) = Unit
  }

  val trustAll = new X509TrustManager {
    override def checkClientTrusted(p1: Array[X509Certificate], p2: String) = Unit

    override def getAcceptedIssuers = null

    override def checkServerTrusted(p1: Array[X509Certificate], p2: String) = Unit
  }

  "Mailer actor" should {
    "act on order creation" in {
      val fakeApp = FakeApplication()
      running(fakeApp) {


        implicit val actorSystem = ActorSystem("testActorSystem", fakeApp.configuration.underlying)
        val mailer = TestActorRef(new Mailer).underlyingActor
        mailer.receive(OrderCreate(Mail(Seq("tsechov@gmail.com"), subject = "foo", message = "bar")))
        success
      }
    }
  }
}
