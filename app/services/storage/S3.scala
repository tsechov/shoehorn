package services.storage

import java.io.ByteArrayInputStream

import com.amazonaws.{HttpMethod, ClientConfiguration}
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GeneratePresignedUrlRequest, PutObjectRequest, CannedAccessControlList, ObjectMetadata}
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

object AmazonS3Communicator {

  import services.ConfigSupport.configKey

  val logger = LoggerFactory.getLogger(this.getClass().getName())
  val credentials = new BasicAWSCredentials(configKey("aws.access.key.id"), configKey("aws.secret.key"))

  val client = new ClientConfiguration()
  client.setSocketTimeout(30000)
  val amazonS3Client = new AmazonS3Client(credentials, client)
  val bucketName = configKey("aws.s3.bucket")

  val pdf = {
    val res = new ObjectMetadata()
    res.setContentType("application/pdf")
    res
  }

}

trait S3Component{
  def getUrl(objectKey: String):String
  def storePdf(objectKey: String, stream: ByteArrayInputStream):Option[String]
}
trait S3 {

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

  def storePdf(objectKey: String, stream: ByteArrayInputStream) = store(objectKey, stream, pdf)

  def store(objectKey: String, stream: ByteArrayInputStream, meta: ObjectMetadata = new ObjectMetadata): Option[String] = {
    try {

      val putObjectRequest = new PutObjectRequest(bucketName, objectKey, stream, meta)
      putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead)
      amazonS3Client.putObject(bucketName, objectKey, stream, meta)
      Some(getUrl(objectKey))
    } catch {
      case ex: Exception => {
        logger.error(ex.getMessage(), ex)
        None
      }

    }
  }
}

