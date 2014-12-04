package controllers

import java.util.UUID

import models.AssetSupport._
import play.api.libs.concurrent.Akka
import play.api.libs.json.Json
import play.api.mvc.{Controller, Action}
import services.ConfigSupport._
import services.mailer.OrderMailer

import services.{MailRequest, OrderUpdateMailRequest, OrderCreateMailRequest, runtime}
import play.api.Play.current

import scala.concurrent.Future
import services.storage.S3Bucket

object Mails extends Controller with S3Bucket {
  lazy val orderPrintService = runtime orderPrintService
  lazy val crudService = runtime crudService
  lazy val mongo = runtime mongo

  val mailAttachmentFolder = configKey("aws.s3.mailattachment.folder", "attachments")


  lazy val mailer = Akka.system.actorOf(OrderMailer.props(mongo, crudService, orderPrintService), name = "mailer")

  def sendOrderCreateMail(orderId: IdType) = Action.async {
    send(orderId, mailAttachmentFolder) {
      (uid, oid, folder) => OrderCreateMailRequest(uid, oid, folder)
    }
  }

  def sendOrderUpdateMail(orderId: IdType) = Action.async {
    send(orderId, mailAttachmentFolder) {
      (uid, oid, folder) => OrderUpdateMailRequest(uid, oid, folder)
    }

  }


  private def send(orderId: IdType, folder: String)(reqFactory: (UUID, String, String) => MailRequest) = {
    val uid = UUID.randomUUID()
    val req = reqFactory(uid, orderId, folder)
    mailer ! req
    Future.successful(Ok(Json.obj("resultUrl" -> req.resultUrl(bucketName))))
  }
}
