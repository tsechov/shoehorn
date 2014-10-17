package services.storage

import java.io.ByteArrayInputStream

import com.amazonaws.services.s3.model.ObjectMetadata
import play.api.test.{FakeApplication, PlaySpecification}


class S3Test extends PlaySpecification {
  "S3 client" should {
    "should be able to store data" in {
      val fakeApp = FakeApplication()
      running(fakeApp) {
        val meta=new ObjectMetadata()
        meta.setContentType("text/plain")
        val res=AmazonS3Communicator.store("foo",new ByteArrayInputStream("bar".getBytes) ,meta)
        res must beSome[String]
      }
    }
  }
}
