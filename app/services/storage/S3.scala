package services.storage

import java.io.ByteArrayInputStream

import com.amazonaws.{HttpMethod, ClientConfiguration}
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GeneratePresignedUrlRequest, PutObjectRequest, CannedAccessControlList, ObjectMetadata}
import org.joda.time.DateTime
import play.api.Logger


object AmazonS3Communicator {

  import services.ConfigSupport.configKey


  val credentials = new BasicAWSCredentials(configKey("aws.access.key.id"), configKey("aws.secret.key"))

  val client = new ClientConfiguration()
  client.setSocketTimeout(30000)
  val amazonS3Client = new AmazonS3Client(credentials, client)
  val bucketName = configKey("aws.s3.bucket")

  def contentTypeMeta(contentType: String) = {
    val res = new ObjectMetadata()
    res.setContentType(contentType)
    res
  }

}

trait RealStorageComponent {
  val realStorage: RealComponentInternal
}

trait RealComponentInternal {
  def getUrl(objectKey: String): String

  def store(objectKey: String, stream: ByteArrayInputStream)(contentType: String): Option[String]
}

trait S3Service extends RealStorageComponent {

  override val realStorage = new RealComponentInternal {
    val s3 = new S3


    override def getUrl(objectKey: String): String = {
      s3.getUrl(objectKey)
    }

    override def store(objectKey: String, stream: ByteArrayInputStream)(contentType: String): Option[String] = {
      s3.store(objectKey, stream)(contentType)
    }
  }
}


class S3 {

  import AmazonS3Communicator._

  def getUrl(objectKey: String) = {

    val oneDayFromNow = new DateTime().plusDays(1).toDate

    val generatePresignedUrlRequest =
      new GeneratePresignedUrlRequest(bucketName, objectKey)
    generatePresignedUrlRequest.setMethod(HttpMethod.GET)
    generatePresignedUrlRequest.setExpiration(oneDayFromNow)

    val url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest)
    url.toString
  }

  def store(objectKey: String, stream: ByteArrayInputStream)(contentType: String) = storeWithMeta(objectKey, stream, contentTypeMeta(contentType))

  private def storeWithMeta(objectKey: String, stream: ByteArrayInputStream, meta: ObjectMetadata = new ObjectMetadata): Option[String] = {
    try {

      val putObjectRequest = new PutObjectRequest(bucketName, objectKey, stream, meta)
      putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead)
      amazonS3Client.putObject(bucketName, objectKey, stream, meta)
      Logger.debug(s"stored object [$bucketName/$objectKey]")
      Some(getUrl(objectKey))
    } catch {
      case ex: Exception => {
        Logger.error(ex.getMessage(), ex)
        None
      }

    }
  }
}

