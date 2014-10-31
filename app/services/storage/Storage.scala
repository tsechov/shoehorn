package services.storage

import java.io.ByteArrayInputStream
import play.api.http.MimeTypes

trait StorageComponent {
  val storage: StorageComponentInternal
}

trait StorageComponentInternal {
  def getUrl(objectKey: String): String

  def store(objectKey: String, stream: ByteArrayInputStream)(contentType: String = MimeTypes.BINARY): Option[String]
}

trait StorageService extends StorageComponent {
  self: RealStorageComponent =>
  override val storage = new StorageComponentInternal {


    def store(objectKey: String, stream: ByteArrayInputStream)(contentType: String = MimeTypes.BINARY) = realStorage.store(objectKey, stream)(contentType)

    override def getUrl(objectKey: String) = realStorage.getUrl(objectKey)
  }
}
