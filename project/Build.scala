

import sbt._
import sbt.File
import sbt.Keys._
import play.Project._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.Version.Bump
import scala.Some

object ApplicationBuild extends Build {
  releaseSettings


  val appName = "shoehorn"
  val appVersion = "NA"

  val appDependencies = Seq(


    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",

    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )


  def bumpType = sys.props.get("versionBump") match {
    case Some("major") => Bump.Major
    case Some("minor") => Bump.Minor
    case _ => Bump.Next
  }

  val main = play.Project(appName, appVersion, appDependencies, settings = Defaults.defaultSettings ++ playScalaSettings ++ releaseSettings).settings(
    sources in(Compile, doc) := Seq.empty,
    publishTo := Some(Resolver.file("file", new File(target.value.absolutePath + "/publish"))),
    version := (version in ThisBuild).value,

    versionBump := bumpType,

    sourceGenerators in Compile <+= Def.task {
      val file = (sourceManaged in Compile).value / "release" / "CurrentVersion.scala"
      IO.write(file, """package release; object CurrentVersion { def apply() = """" + (version in ThisBuild).value + """" }""")
      Seq(file)
    },
    resourceGenerators in Compile <+= Def.task {
      val file = (resourceManaged in Compile).value / "public" / "api" / "api.html"
      Seq(file)
    }

  )


}
