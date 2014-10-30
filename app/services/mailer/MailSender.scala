package services.mailer

import akka.actor.Actor
import org.apache.commons.mail._
import services.mailer.order.support.Mail
import scala.util.Try

class MailSender extends Actor {

  System.setProperty("mail.debug", "true")
  System.setProperty("mail.smtp.socketFactory.class", "services.mailer.DummySSLSocketFactory")

  import services.ConfigSupport.configKey

  private val host = configKey("smtp.host")
  private val port = configKey("smtp.port")
  private val user = configKey("smtp.user")
  private val password = configKey("smtp.password")
  private val fromName = configKey("ordermail.from.name")
  private val fromEmail = configKey("ordermail.from.email")


  override def receive = {
    case mail: Mail => {
      sender ! Try(sendMail(mail))
    }

  }


  def sendMail(mail: Mail) = {
    //FIXME: sort attachments out
    val commonsMail: Email = if (mail.attachment.isDefined) {
      val attachment = new EmailAttachment()
      attachment.setPath(mail.attachment.get.getAbsolutePath)
      attachment.setDisposition(EmailAttachment.ATTACHMENT)
      attachment.setName("foo.bar")
      new MultiPartEmail().attach(attachment).setMsg(mail.message)
    } else if (mail.richMessage.isDefined) {
      new HtmlEmail().setHtmlMsg(mail.richMessage.get).setTextMsg(mail.message)
    } else {
      new SimpleEmail().setMsg(mail.message)
    }


    commonsMail.setHostName(host)
    commonsMail.setSmtpPort(port.toInt)
    commonsMail.setDebug(true)
    commonsMail.setStartTLSEnabled(true)
    commonsMail.setSSLOnConnect(false)
    commonsMail.setAuthenticator(new DefaultAuthenticator(user, password))


    mail.to.foreach(commonsMail.addTo(_))
    mail.cc.foreach(commonsMail.addCc(_))
    mail.bcc.foreach(commonsMail.addBcc(_))

    val preparedMail = commonsMail.
      setFrom(fromEmail, fromName).
      setSubject(mail.subject)


    preparedMail.send
  }
}


