package controllers

import play.api.libs.concurrent.Akka
import services.mailer.OrderMailer
import services.reporting.ReportGenerator
import services.runtime
import play.api.Play.current

object Mails {
  lazy val orderPrintService = runtime orderPrintService
  lazy val crudService = runtime crudService


  lazy val mailer = Akka.system.actorOf(OrderMailer.props(crudService,orderPrintService), name = "mailer")

}
