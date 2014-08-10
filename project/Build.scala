
import sbt._
import sbt.Keys._
import play.Project._
import sbtrelease.ReleasePlugin._

object ApplicationBuild extends Build {
  releaseSettings

  val appName = "shoehorn-backend"
  val appVersion = "NA"

  val appDependencies = Seq(
    "com.google.inject" % "guice" % "3.0",
    "javax.inject" % "javax.inject" % "1",
    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",

    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies, settings = Defaults.defaultSettings ++ playScalaSettings ++ releaseSettings).settings(
    sources in(Compile, doc) := Seq.empty,
    publishTo := Some(Resolver.file("file", new File(target.value.absolutePath + "/publish")))
  )


}
