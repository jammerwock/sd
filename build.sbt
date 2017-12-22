name := """sd"""

version := "1.1"

scalaVersion := "2.12.4"

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  guice,
  filters,
  //  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
  //  "org.webjars" % "bootstrap" % "3.3.7-1" exclude("org.webjars", "jquery"),
  //  "org.webjars" % "jquery" % "3.2.1",
  //  "org.webjars" % "font-awesome" % "4.7.0",
  //  "org.webjars" % "bootstrap-datepicker" % "1.4.0" exclude("org.webjars", "bootstrap"),

  //  "org.webjars.bower" % "OwlCarousel2" % "2.2.1",
  //  "org.webjars" % "bootstrap" % "4.0.0-beta",
  "com.github.marcospereira" %% "play-hocon-i18n" % "1.0.1",
  //  "org.webjars" %% "webjars-play" % "2.6.1"
  "com.github.mauricio" %% "postgresql-async" % "0.2.21",
  "net.codingwell" %% "scala-guice" % "4.1.0"
)

scalacOptions ++= Seq(
  "-Xprint:typer"
)