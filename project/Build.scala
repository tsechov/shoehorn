
import sbt._
import sbt.Keys._
import play.Project._
import sbtrelease.ReleasePlugin._
import scala.Some
import scala.Some

object ApplicationBuild extends Build {
  releaseSettings

  val appName = "shoehorn"
  val appVersion = "NA"

  val appDependencies = Seq(
    "com.github.fge" % "json-schema-validator" % "2.2.5",

    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",

    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies, settings = Defaults.defaultSettings ++ playScalaSettings ++ releaseSettings).settings(
    sources in(Compile, doc) := Seq.empty,
    publishTo := Some(Resolver.file("file", new File(target.value.absolutePath + "/publish"))),
    version := (version in ThisBuild).value,
    sourceGenerators in Compile <+= Def.task {
      val file = (sourceManaged in Compile).value / "release" / "CurrentVersion.scala"
      IO.write(file, """package release; object CurrentVersion { def apply() = """" + (version in ThisBuild).value + """" }""")
      Seq(file)
    }
  )


}
