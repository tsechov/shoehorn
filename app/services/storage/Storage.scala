package services.storage

import java.io.ByteArrayInputStream

trait StorageComponent{
  val storage:StorageComponentInternal
}

trait StorageComponentInternal{
  def getUrl(objectKey: String):String
  def storePdf(objectKey: String, stream: ByteArrayInputStream):Option[String]
}

trait StorageService extends StorageComponent{
  self:RealStorageComponent =>
  override val storage=new StorageComponentInternal {
    override def storePdf(objectKey: String, stream: ByteArrayInputStream)= realStorage.storePdf(objectKey,stream)


    override def getUrl(objectKey: String)=realStorage.getUrl(objectKey)
  }
}
