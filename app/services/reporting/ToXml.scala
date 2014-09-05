package services.reporting

import com.thoughtworks.xstream._
import com.thoughtworks.xstream.io.xml.DomDriver
import net.mixedbits.tools.XStreamConversions
import models.order.SortimentItem
import scala.xml._
import scala.xml.transform.{RuleTransformer, RewriteRule}
import scala.io.Source
import models.order.OrderReport

object ToXml {

  def get(source: OrderReport): String = {

    val xstream = XStreamConversions(new XStream(new DomDriver()))


    val xmlString = xstream.toXML(source)

    val xml = XML.loadString(xmlString)

    val replaced = replaceSortiments(xml)

    val pp = new PrettyPrinter(123, 2).format(_: Node)

    pp(replaced)
  }

  def replaceSortiments(dom: Node): Node = {

    object replaceSortiments extends RewriteRule {

      override def transform(n: Node): Seq[Node] = n match {

        case e: Elem if (e.label == "sortimentitem") => {
          val size = e.child.find(_.label == "size").map(_.text).getOrElse("NA")
          e.copy(label = "sortimentitem" + size)
        }
        case other => other

      }

    }

    object transform extends RuleTransformer(replaceSortiments)

    transform(dom)


  }

}
