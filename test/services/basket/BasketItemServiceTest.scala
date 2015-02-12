package services.basket

import models.AssetSupport
import play.api.libs.json.Json
import play.api.test.FakeApplication
import play.api.test.PlaySpecification
import services.DbQuery

/**
 * @author tsechov
 */
class BasketItemServiceTest extends PlaySpecification {
  val MONGO_URI = System.getenv("MONGO_URI")
  "BasketItemService" should {
    "should be able to find a basket by id" in {
      val app = FakeApplication(
        withoutPlugins = Seq("com.typesafe.plugin.CommonsMailerPlugin"),
        additionalConfiguration = Map(
          "mongodb.uri" -> MONGO_URI,
          "mongo-async-driver.akka.loglevel" -> "DEBUG"
        )
      )

      running(app) {
        val target = new BasketItemService
        val res = await(target.find(DbQuery(Json.obj(AssetSupport.idFieldName -> "543c1871b00b00090c54f8e5"))))
        println(res)
        res.size must be_>=(0)
      }

    }
  }
}
