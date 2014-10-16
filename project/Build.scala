

import sbt._
import sbt.File
import sbt.Keys._
import play.Project._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease._
import sbtrelease.Version.Bump
import scala.Some

object ApplicationBuild extends Build {
  releaseSettings


  val appName = "shoehorn"
  val appVersion = "NA"


  val bad = Seq(
    ExclusionRule(name = "log4j"),
    ExclusionRule(name = "commons-logging"),
    ExclusionRule(name = "commons-collections"),
    ExclusionRule(organization = "org.slf4j")
  )

  val appDependencies = Seq(


    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",
    "org.codehaus.jettison" % "jettison" % "1.3.5",
    "com.thoughtworks.xstream" % "xstream" % "1.4.7",
    "net.sf.jasperreports" % "jasperreports" % "5.2.0" excludeAll (bad: _*),
    "commons-collections" % "commons-collections" % "2.1",
    "jaxen" % "jaxen" % "1.1.6",
    "org.scalaz" %% "scalaz-effect" % "7.0.0",
    "org.apache.poi" % "poi" % "3.9",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.1" % "test",
    "org.scalatest" % "scalatest_2.10" % "2.2.2"
  )

  //FIXME: sort out versionbumping
  def bumpType = sys.props.get("versionBump") match {
    case Some("major") => Bump.Major
    case Some("minor") => Bump.Minor
    case _ => Bump.Next
  }

  def versionBump(version: Version) = sys.props.get("versionBump") match {
    case Some("major") => version.bumpMajor
    case Some("minor") => version.bumpMinor
    case _ => version.bumpBugfix
  }


  val main = play.Project(appName, appVersion, appDependencies, settings = Defaults.defaultSettings ++ playScalaSettings ++ releaseSettings).settings(
    resolvers ++= Seq("Jasper Community" at "http://jasperreports.sourceforge.net/maven2", "pentaho" at "http://repository.pentaho.org/artifactory/pentaho"),
    sources in(Compile, doc) := Seq.empty,
    publishTo := Some(Resolver.file("file", new File(target.value.absolutePath + "/publish"))),
    version := (version in ThisBuild).value,

    nextVersion := {
      ver => Version(ver).map(versionBump(_).asSnapshot.string).getOrElse(versionFormatError)
    },

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
