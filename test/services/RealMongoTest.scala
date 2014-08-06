package services

import org.specs2.mutable._
import org.specs2.mock.Mockito
import org.mockito.Mockito._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.test.{FakeApplication, PlaySpecification}

class RealMongoTest extends PlaySpecification with Mockito {

  "RealMongo" should {
    "should be mockeable" in {

      implicit val app = mock[play.api.Application]
      val target = new RealMongo {
        override def collection(name: String): JSONCollection = mock[JSONCollection]
      }
      success

    }
  }
}
