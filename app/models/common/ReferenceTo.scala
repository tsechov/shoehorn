package models.common

import models.AssetSupport

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError
import models.customer.AgentIn


case class ReferenceTo[A](id: String)

////object ReferenceTo extends Ref[AgentIn] {
////
////
//}

trait Referable[A] {
  val reads = new Reads[ReferenceTo[A]] {
    override def reads(json: JsValue) = json match {
      case JsString(s) => JsSuccess[ReferenceTo[A]](ReferenceTo(s))
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
    }
  }

  val writes = new Writes[ReferenceTo[A]] {
    override def writes(o: ReferenceTo[A]) = JsString(o.id)
  }

  implicit val referenceFormat = Format[ReferenceTo[A]](reads, writes)
}
