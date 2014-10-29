package controllers

import java.util.UUID


import controllers.utils.S3Bucket
import models.AssetSupport._
import play.api.libs.concurrent.Akka
import play.api.libs.json.Json
import play.api.mvc.{Controller, Action}
import services.ConfigSupport._
import services.mailer.OrderMailer

import services.{OrderMailRequest, OrderReportRequest, runtime}
import play.api.Play.current

import scala.concurrent.Future

object Mails extends Controller with S3Bucket{
  lazy val orderPrintService = runtime orderPrintService
  lazy val crudService = runtime crudService

  val mailAttachmentFolder=configKey("aws.s3.mailattachment.folder","attachments")


  lazy val mailer = Akka.system.actorOf(OrderMailer.props(crudService,orderPrintService), name = "mailer")

  def ordermail(orderId:IdType) = Action.async {

    val uid=UUID.randomUUID()
    val req=OrderMailRequest(uid,orderId,mailAttachmentFolder)
    mailer ! req
    Future.successful(Ok)
  }
}
