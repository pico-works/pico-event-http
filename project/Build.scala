import sbt.Keys._
import sbt._

object Build extends sbt.Build {
  val pico_disposal     = "org.pico"                  %%  "pico-disposal"           % "1.0.5"
  val pico_event        = "org.pico"                  %%  "pico-event"              % "3.0.1"
  val httpasyncclient   = "org.apache.httpcomponents" %   "httpasyncclient"         % "4.1.2"

  val specs2_core       = "org.specs2"                %%  "specs2-core"             % "3.7.2"
  val typesafe_config   = "com.typesafe"              %   "config"                  % "1.3.1"

  implicit class ProjectOps(self: Project) {
    def standard(theDescription: String) = {
      self
          .settings(scalacOptions in Test ++= Seq("-Yrangepos"))
          .settings(publishTo := Some("Releases" at "s3://dl.john-ky.io/maven/releases"))
          .settings(description := theDescription)
          .settings(isSnapshot := true)
          .settings(addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.0" cross CrossVersion.binary))
    }

    def notPublished = self.settings(publish := {}).settings(publishArtifact := false)

    def it = self.configs(IntegrationTest).settings(Defaults.itSettings: _*)

    def libs(modules: ModuleID*) = self.settings(libraryDependencies ++= modules)

    def testLibs(modules: ModuleID*) = self.libs(modules.map(_ % "it,test"): _*)

    def itLibs(modules: ModuleID*) = self.libs(modules.map(_ % "it"): _*)
  }

  lazy val `pico-event-http-client` = Project(id = "pico-event-http-client", base = file("pico-event-http-client"))
      .standard("pico-event shim library for event-http").it
      .libs(pico_disposal, httpasyncclient, pico_event)
      .testLibs(specs2_core)
      .itLibs(typesafe_config)

  lazy val all = Project(id = "pico-event-http-client-project", base = file("."))
      .notPublished
      .aggregate(`pico-event-http-client`)
}
