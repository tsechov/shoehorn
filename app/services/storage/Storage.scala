package services.storage

import java.io.ByteArrayInputStream
import play.api.http.MimeTypes

case class StreamAndLength(stream: ByteArrayInputStream, length: Long, contentType: String = MimeTypes.BINARY)

trait StorageComponent {
  val storage: StorageComponentInternal
}

trait StorageComponentInternal {
  def getUrl(objectKey: String): String

  def store(objectKey: String, content: StreamAndLength): Option[String]
}

trait StorageService extends StorageComponent {
  self: RealStorageComponent =>
  override val storage = new StorageComponentInternal {


    def store(objectKey: String, content: StreamAndLength) = realStorage.store(objectKey, content)

    override def getUrl(objectKey: String) = realStorage.getUrl(objectKey)
  }
}
