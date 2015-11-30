import sbt.Keys._
import sbtunidoc.Plugin.UnidocKeys._
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

parallelExecution in ThisBuild := false
lazy val aggregated = taskKey[Unit]("Print currently aggregated tasks under the root.")

lazy val buildSettings = Seq(
  name := "rainie",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.10.5", "2.11.7")
)


lazy val versions = new {
  val finagle = "6.30.0"
  val twitterServer = "1.15.0"
  val logback = "1.0.13"
  val spring = "3.2.15.RELEASE"
  val slf4j = "1.7.12"
  val scrooge = "4.2.0"
  val grizzled = "1.0.2"

  val commonsIo = "2.4"
  val jodaTime = "2.5"
}

lazy val compilerOptions = scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen"
) ++ (
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 11)) => Seq("-Ywarn-unused-import")
    case _ => Seq.empty
  })

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact := true,
  publishArtifact in Test := false,
  publishArtifact in (Compile, packageDoc) := true,
  publishArtifact in (Test, packageDoc) := true,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "http://123.57.227.107:8086/nexus/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  autoAPIMappings := true,
  pomPostProcess := { (node: scala.xml.Node) =>
    val rule = new scala.xml.transform.RewriteRule {
      override def transform(n: scala.xml.Node): scala.xml.NodeSeq =
        n.nameToString(new StringBuilder()).toString() match {
          case "dependency" if (n \ "groupId").text.trim == "org.scoverage" => Nil
          case _ => n
        }
    }
    new scala.xml.transform.RuleTransformer(rule).transform(node).head
  }
)

val baseSettings = Seq(
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % versions.logback % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "org.scalatest" %% "scalatest" % "2.2.3" % "test",
    "org.specs2" %% "specs2" % "2.3.12" % "test"
  ),
  resolvers ++= Seq(
    Resolver.mavenLocal,
    "tc-nexus" at "http://123.57.227.107:8086/nexus/content/groups/tftiancai-nexus-group",
    "oschina2" at "http://maven.oschina.net/content/groups/public",
    Resolver.sonatypeRepo("releases")
  ),
  compilerOptions,
  javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
)

lazy val commonSettings = baseSettings ++ publishSettings ++ buildSettings ++ unidocSettings

lazy val rainieSettings = baseSettings ++ publishSettings ++ buildSettings ++ Seq(
  organization := "com.itiancai.rainie"
)

lazy val baseServerBuildSettings = baseSettings ++ buildSettings ++ publishSettings ++ Seq(
  publishLocal := {},
  publish := {},
  assemblyMergeStrategy in assembly := {
    case "BUILD" => MergeStrategy.discard
    case other => MergeStrategy.defaultMergeStrategy(other)
  }
)

lazy val exampleServerBuildSettings = baseServerBuildSettings ++ Seq(
  organization := "com.itiancai.rainie.example",
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % versions.logback)
)

lazy val aggregatedProjects = {
  rainieModules ++ exampleModules
}

lazy val rainieModules = Seq(
  inject,
  thrift,
  slf4j
)

lazy val exampleModules =
  Seq(
    thriftExampleIdl,
    thriftExampleServer
  )

lazy val root = (project in file(".")).
  settings(baseSettings).
  settings(buildSettings).
  settings(publishSettings).
  settings(unidocSettings).
  settings(
    organization := "com.itiancai.rainie",
    moduleName := "rainie-root",
    unidocProjectFilter in(ScalaUnidoc, unidoc) := inAnyProject,
    aggregated := {
      println(aggregatedProjects.map(_.id).mkString("\n"))
    }
  ).aggregate(aggregatedProjects.map(x => x: ProjectReference): _*)


lazy val inject = (project in file("inject")).
  settings(rainieSettings).
  settings(
    name := "inject",
    libraryDependencies ++= Seq(
      "org.springframework" % "spring-context" % versions.spring,
      "com.twitter" %% "finagle-core" % versions.finagle,
      "com.twitter" %% "finagle-stats" % versions.finagle,
      "com.twitter" %% "twitter-server" % versions.twitterServer,
      "ch.qos.logback" % "logback-classic" % versions.logback,
      "org.clapper" %% "grizzled-slf4j" % versions.grizzled,
      "org.slf4j" % "jcl-over-slf4j" % versions.slf4j,
      "org.slf4j" % "jul-to-slf4j" % versions.slf4j,
      "commons-io" % "commons-io" % versions.commonsIo,
      "joda-time" % "joda-time" % versions.jodaTime
    ),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  )

lazy val thrift = project.
  settings(rainieSettings).
  settings(
    name := "rainie-thrift",
    moduleName := "rainie-thrift",
    libraryDependencies ++= Seq(
      "com.twitter" %% "scrooge-core" % versions.scrooge,
      "com.twitter" %% "finagle-thrift" % versions.finagle
    )
  ).
  dependsOn(
    inject
  )

lazy val slf4j = project.
  settings(rainieSettings).
  settings(
    name := "rainie-slf4j",
    moduleName := "rainie-slf4j"
  ).
  dependsOn(
    inject
  )

lazy val thriftExampleIdl = (project in file("examples/thrift-server/thrift-example-idl")).
  settings(baseServerBuildSettings).
  settings(
    name := "thrift-example-idl",
    moduleName := "thrift-example-idl",
    ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "<empty>;.*\\.thriftscala.*",
    scroogeThriftIncludeFolders in Compile := Seq(file("thrift/src/main/thrift"))
  ).
  dependsOn(thrift)

lazy val thriftExampleServer = (project in file("examples/thrift-server/thrift-example-server")).
  settings(exampleServerBuildSettings).
  settings(
    name := "thrift-example-server",
    moduleName := "thrift-example-server",
    coverageExcludedPackages := "<empty>;.*ExceptionTranslationFilter.*",
    scroogeThriftIncludeFolders in Compile := Seq(
      file("thrift/src/main/thrift"),
      file("examples/thrift-server/thrift-example-idl/src/main/thrift")
    )
  ).
  dependsOn(
    thriftExampleIdl,
    slf4j,
    thrift
  )




    