package services

import java.util.concurrent.TimeUnit

import models.AssetSupport
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.libs.json.{Json, Reads}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}


class ServicesTest extends Specification with Mockito {


  "RealRepositoryComponent" should {
    "should be mockeable" in {
      type Test = String
      val mockedMongo = mock[MongoDb]

      val target: CrudRepository = new CrudRepository with Mongo {
        override val mongo = mockedMongo

      }

      val expectedId = "blah"
      val query = Json.obj(AssetSupport.idFieldName -> expectedId)
      val expectedResultList = List(Json.obj("foo" -> "bar"))
      implicit val colName = mock[CollectionName[Test]]
      implicit val reads = mock[Reads[Test]]

      when(mockedMongo.find[Test](query)).thenReturn(Future.successful(expectedResultList))



      val result = target.crudRepository.getById[Test](DbQuery(query))
      val timeout: FiniteDuration = FiniteDuration(5, TimeUnit.SECONDS)
      Await.result(result, timeout) match {
        case Some(value) => success //value must beEqualTo("foo")
        case _ => failure
      }


    }
  }
}
