

import java.io.File
import sbt._
import sbt.File
import sbt.Keys._
import play.Project._
import sbtrelease.ReleasePlugin._
import org.eigengo.sbtraml.RamlPlugin.Keys._
import scala.Some

object ApplicationBuild extends Build {
  releaseSettings
  org.eigengo.sbtraml.RamlPlugin.settings

  val appName = "shoehorn"
  val appVersion = "NA"

  val appDependencies = Seq(


    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",

    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )


  lazy val versionReport = taskKey[String]("version-report")


  lazy val versionSettings = versionReport <<= (externalDependencyClasspath in Compile, streams) map {
    (cp: Seq[Attributed[File]], streams) =>
      val report = cp.map {
        attributed =>
          attributed.get(Keys.moduleID.key) match {
            case Some(moduleId) => "%40s %20s %10s %10s".format(
              moduleId.organization,
              moduleId.name,
              moduleId.revision,
              moduleId.configurations.getOrElse("")
            )
            case None =>
              // unmanaged JAR, just
              attributed.data.getAbsolutePath
          }
      }.mkString("\n")
      streams.log.info(report)
      report
  }


  lazy val helloTask = taskKey[Unit]("Prints 'Hello World'")

  lazy val helloSettings = helloTask := {
    println("Hello World")
    val file = (resourceManaged in Compile).value / "public" / "api" / ".placeholder"
    IO.write(file, "#here for placeholding")
  }


  val main = play.Project(appName, appVersion, appDependencies, settings = Defaults.defaultSettings ++ playScalaSettings ++ releaseSettings ).settings(
    sources in(Compile, doc) := Seq.empty,
    publishTo := Some(Resolver.file("file", new File(target.value.absolutePath + "/publish"))),
    version := (version in ThisBuild).value,


    sourceGenerators in Compile <+= Def.task {
      val file = (sourceManaged in Compile).value / "release" / "CurrentVersion.scala"
      IO.write(file, """package release; object CurrentVersion { def apply() = """" + (version in ThisBuild).value + """" }""")
      Seq(file)
    },
    resourceGenerators in Compile <+= Def.task {
      val file = (resourceManaged in Compile).value / "public" / "api" / "api.html"
      Seq(file)
    }
//    ,
//    source in Raml := new File((baseDirectory in Compile).value.absolutePath + "/public/api"),
    //    target in Raml := (resourceManaged in Compile).value / "public" / "api"
  )


}
