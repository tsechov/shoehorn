package services.storage

import java.io.ByteArrayInputStream

import com.amazonaws.services.s3.model.ObjectMetadata
import play.api.test.{FakeApplication, PlaySpecification}
import play.api.http.MimeTypes


class S3Test extends PlaySpecification {
  "S3 client" should {
    "should be able to store data" in {
      val fakeApp = FakeApplication()
      running(fakeApp) {
        val meta = new ObjectMetadata()
        meta.setContentType("text/plain")
        val datas = "bar".getBytes
        val res = new S3().store("foo", StreamAndLength(new ByteArrayInputStream(datas), datas.length, MimeTypes.TEXT))
        res must beSome[String]
      }
    }
  }
}
