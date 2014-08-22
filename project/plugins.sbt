// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype Snapshots Repository" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Mulesoft Releases Repository" at "https://repository-master.mulesoft.org/nexus/content/repositories/releases"

resolvers += Resolver.url(
  "sbt-plugin-releases", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
)(Resolver.ivyStylePatterns)

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1" withSources)

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.4" withSources)

addSbtPlugin("org.eigengo" % "sbt-raml" % "0.1-SNAPSHOT" exclude("commons-logging", "commons-logging") exclude("org.slf4j", "slf4j-log4j12"))

libraryDependencies += "org.slf4j" % "jcl-over-slf4j" % "1.7.5"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.13"