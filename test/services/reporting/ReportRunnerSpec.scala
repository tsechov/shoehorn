package services.reporting

import org.specs2.mutable.Specification
import org.specs2.execute.Result
import java.io.{File, BufferedInputStream, FileInputStream, FileOutputStream}
import scala.io.Source

/**
 * @author janmachacek
 */
case class User(username: String, firstName: String, lastName: String)

class ReportRunnerSpec extends Specification with ReportFormats {

  val runner = new ReportRunner with JRXmlReportCompiler with ClasspathResourceReportLoader

  //  "failure collection" should {
  //    "report errors in loader" in {
  //      runner.runReportT("foo")(PdfDS).run.isLeft mustEqual true
  //    }
  //
  //    "report errors in compiler" in {
  //      runner.runReportT("broken.jrxml")(PdfDS).run.isLeft mustEqual true
  //    }
  //  }

  "simple report" in {
    runReport("/empty.jrxml", EmptyExpression, XmlParameterExpression("conf/order2.xml"))


  }

  def runReport(source: String, parametersExpression: Expression, dataSourceExpression: DataSourceExpression): Result = {
    println(new File(".").getAbsolutePath)
    runner.runReportT(source)(PdfDS, parametersExpression, dataSourceExpression).run.toEither match {
      case Left(e) => failure(e.getMessage)
      case Right(pdf) =>
        val fos = new FileOutputStream("target/x.pdf")
        fos.write(pdf)
        fos.close()
        success
    }
  }

}
