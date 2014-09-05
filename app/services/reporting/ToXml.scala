package services.reporting

import com.thoughtworks.xstream._
import com.thoughtworks.xstream.io.xml.DomDriver
import net.mixedbits.tools.XStreamConversions
import models.order.{SortimentItem, OrderReport}
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

    // You can define objects (or classes) inside functions.

    object replaceSortiments extends RewriteRule {
      // The rule replaces any "features" element with a new
      // features element we build for the occasion.

      override def transform(n: Node): Seq[Node] = n match {

        // match on the "features" element

        case e: Elem if (e.label == "sortimentitem") => {

          // XML literals again...

          // Can embed scala inside an XML literal: In this case,
          // apply an anonymous function over the list of features
          // in the original parameter list of the replaceFeatures
          // function. The func turns a feature name into a feature
          // node.
          //e.copy(label = "csihajj")

          val size = e.child.find(_.label == "size").map(_.text).getOrElse("NA")
          e.copy(label = "sortimentitem" + size)
        }

        // That which we cannot speak of, we must pass over
        // in silence....

        case other => {

          other
        }
      }

    }

    object transform extends RuleTransformer(replaceSortiments)

    transform(dom)


  }

}
