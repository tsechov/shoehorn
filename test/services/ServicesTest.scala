package services

import org.specs2.mutable._
import org.specs2.mock.Mockito
import org.mockito.Mockito._
import models.AssetSupport
import scala.concurrent.{Await, Future}
import play.api.libs.json.{Reads, Json}
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit


class ServicesTest extends Specification with Mockito{



  "RealRepositoryComponent" should {
    "should be mockeable" in {
      type Test=String
      val mockedMongo=mock[Mongo]

      val target:RealRepositoryComponent = new RealRepositoryComponent with MongoComponent{
        override val mongo=mockedMongo

      }

      val expectedId=Json.obj("foo"->"blah")
      val query = Json.obj(AssetSupport.idFieldName -> expectedId)
      val expectedResultList=List("foo")
      implicit val colName=mock[CollectionName[Test]]
      implicit val reads=mock[Reads[Test]]
      when(mockedMongo.find[Test](query)).thenReturn(Future.successful(expectedResultList))



      val result=target.repository.getById(expectedId)
      val timeout: FiniteDuration = FiniteDuration(5, TimeUnit.SECONDS)
      Await.result(result, timeout) match {
        case Some(value) => success//value must beEqualTo("foo")
        case None => failure
      }




    }
  }
}
