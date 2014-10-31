package services.mailer

import org.specs2.mutable.Specification

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.testkit.TestActorRef
import play.api.libs.json.Json
import play.api.test.{PlaySpecification, FakeApplication}
import javax.net.ssl.{X509TrustManager, TrustManager, SSLContext}
import java.security.cert.X509Certificate
import services.mailer.order.support.Mail

class MailerActorTest extends PlaySpecification {


  "Mailer actor" should {
    "act on order creation" in {
      val fakeApp = FakeApplication()
      running(fakeApp) {


        implicit val actorSystem = ActorSystem("testActorSystem", fakeApp.configuration.underlying)
        val mailer = TestActorRef(new MailSender).underlyingActor
        mailer.receive(Mail(List("tsechov@gmail.com"), subject = "foo", message = "bar"))
        success
      }
    }
  }
}
