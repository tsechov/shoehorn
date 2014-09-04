package services.reporting

import com.thoughtworks.xstream._
import com.thoughtworks.xstream.io.xml.DomDriver
import net.mixedbits.tools.XStreamConversions

object ToXml {

  def get[A](source: A): String = {

    val xstream = XStreamConversions(new XStream(new DomDriver()))


    xstream.toXML(source)

  }


}
