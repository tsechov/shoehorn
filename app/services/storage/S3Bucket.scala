package services.storage

import services.ConfigSupport._

trait S3Bucket {
  val bucketName=configKey("aws.s3.bucket")
}
