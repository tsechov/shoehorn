package services.basket

import models.AssetSupport
import play.api.libs.json.Json
import play.api.test.FakeApplication
import play.api.test.PlaySpecification
import scala.concurrent.duration._
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
          "mongodb.uri" -> MONGO_URI
        )
      )

      running(app) {
        val testInput = Json.fromJson[BasketIn2](json).get
        val target = new BasketItemService
        //543c1871b00b00090c54f8e5
        //val res = await(target.find(DbQuery(Json.obj(AssetSupport.idFieldName -> "543c1871b00b00090c54f8e5"))))
        //val res = await(target.find[BasketIn2](DbQuery(limit = 1)))(15 seconds)
        val res = await(target.updateItems(testInput))(15 seconds)
        println(res)
        res must_==()
      }

    }
  }

  def json = Json.parse(
    """
      |{
      |    "_id" : "543c1871b00b00090c54f8e5",
      |    "createdAt" : "2014-10-13T18:22:41.309+00:00",
      |    "lastModifiedAt" : "2014-10-13T18:27:10.952+00:00",
      |    "active" : false,
      |    "description" : "5406dacb2a00000d0c3d46da",
      |    "originatorId" : "5406dacb2a00000d0c3d46da",
      |    "items" : [
      |        {
      |            "product" : {
      |                "_id" : "5424816d7101009e09b2b99b",
      |                "createdAt" : "2014-09-25T20:56:13.656+00:00",
      |                "lastModifiedAt" : "2014-09-25T20:56:13.656+00:00",
      |                "active" : true,
      |                "description" : "",
      |                "name" : "",
      |                "itemNumber" : "3103-63172",
      |                "image" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-full.jpg",
      |                "imageThumb" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-thumb.jpg",
      |                "catalogs" : [
      |                    {
      |                        "catalogId" : "5406c32c2a000085093d3da7",
      |                        "sizeGroups" : [
      |                            {
      |                                "sizeGroupId" : "5406b8e12a00006e083d3d93",
      |                                "unitPrice" : 6100
      |                            }
      |                        ]
      |                    }
      |                ]
      |            },
      |            "quantity" : 2,
      |            "size" : 25
      |        },
      |        {
      |            "product" : {
      |                "_id" : "5424816d7101009e09b2b99b",
      |                "createdAt" : "2014-09-25T20:56:13.656+00:00",
      |                "lastModifiedAt" : "2014-09-25T20:56:13.656+00:00",
      |                "active" : true,
      |                "description" : "",
      |                "name" : "",
      |                "itemNumber" : "3103-63172",
      |                "image" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-full.jpg",
      |                "imageThumb" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-thumb.jpg",
      |                "catalogs" : [
      |                    {
      |                        "catalogId" : "5406c32c2a000085093d3da7",
      |                        "sizeGroups" : [
      |                            {
      |                                "sizeGroupId" : "5406b8e12a00006e083d3d93",
      |                                "unitPrice" : 6100
      |                            }
      |                        ]
      |                    }
      |                ]
      |            },
      |            "quantity" : 1,
      |            "size" : 26
      |        },
      |        {
      |            "product" : {
      |                "_id" : "5424816d7101009e09b2b99b",
      |                "createdAt" : "2014-09-25T20:56:13.656+00:00",
      |                "lastModifiedAt" : "2014-09-25T20:56:13.656+00:00",
      |                "active" : true,
      |                "description" : "",
      |                "name" : "",
      |                "itemNumber" : "3103-63172",
      |                "image" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-full.jpg",
      |                "imageThumb" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-thumb.jpg",
      |                "catalogs" : [
      |                    {
      |                        "catalogId" : "5406c32c2a000085093d3da7",
      |                        "sizeGroups" : [
      |                            {
      |                                "sizeGroupId" : "5406b8e12a00006e083d3d93",
      |                                "unitPrice" : 6100
      |                            }
      |                        ]
      |                    }
      |                ]
      |            },
      |            "quantity" : 1,
      |            "size" : 27
      |        },
      |        {
      |            "product" : {
      |                "_id" : "5424816d7101009e09b2b99b",
      |                "createdAt" : "2014-09-25T20:56:13.656+00:00",
      |                "lastModifiedAt" : "2014-09-25T20:56:13.656+00:00",
      |                "active" : true,
      |                "description" : "",
      |                "name" : "",
      |                "itemNumber" : "3103-63172",
      |                "image" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-full.jpg",
      |                "imageThumb" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-thumb.jpg",
      |                "catalogs" : [
      |                    {
      |                        "catalogId" : "5406c32c2a000085093d3da7",
      |                        "sizeGroups" : [
      |                            {
      |                                "sizeGroupId" : "5406b8e12a00006e083d3d93",
      |                                "unitPrice" : 6100
      |                            }
      |                        ]
      |                    }
      |                ]
      |            },
      |            "quantity" : 1,
      |            "size" : 28
      |        },
      |        {
      |            "product" : {
      |                "_id" : "5424816d7101009e09b2b99b",
      |                "createdAt" : "2014-09-25T20:56:13.656+00:00",
      |                "lastModifiedAt" : "2014-09-25T20:56:13.656+00:00",
      |                "active" : true,
      |                "description" : "",
      |                "name" : "",
      |                "itemNumber" : "3103-63172",
      |                "image" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-full.jpg",
      |                "imageThumb" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-thumb.jpg",
      |                "catalogs" : [
      |                    {
      |                        "catalogId" : "5406c32c2a000085093d3da7",
      |                        "sizeGroups" : [
      |                            {
      |                                "sizeGroupId" : "5406b8e12a00006e083d3d93",
      |                                "unitPrice" : 6100
      |                            }
      |                        ]
      |                    }
      |                ]
      |            },
      |            "quantity" : 1,
      |            "size" : 29
      |        },
      |        {
      |            "product" : {
      |                "_id" : "5424816d7101009e09b2b99b",
      |                "createdAt" : "2014-09-25T20:56:13.656+00:00",
      |                "lastModifiedAt" : "2014-09-25T20:56:13.656+00:00",
      |                "active" : true,
      |                "description" : "",
      |                "name" : "",
      |                "itemNumber" : "3103-63172",
      |                "image" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-full.jpg",
      |                "imageThumb" : "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/3103-63172-thumb.jpg",
      |                "catalogs" : [
      |                    {
      |                        "catalogId" : "5406c32c2a000085093d3da7",
      |                        "sizeGroups" : [
      |                            {
      |                                "sizeGroupId" : "5406b8e12a00006e083d3d93",
      |                                "unitPrice" : 6100
      |                            }
      |                        ]
      |                    }
      |                ]
      |            },
      |            "quantity" : 3,
      |            "size" : 30
      |        }
      |    ]
      |}
    """.stripMargin)
}
