import sbt.Keys._
import sbtunidoc.Plugin.UnidocKeys._
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

parallelExecution in ThisBuild := false
lazy val aggregated = taskKey[Unit]("Print currently aggregated tasks under the root.")

lazy val buildSettings = Seq(
  name := "rainie",
  version := "0.0.5",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.10.5", "2.11.7")
)

lazy val versions = new {

  val finagle = "6.34.0"
  val scrooge = "4.6.0"
  val twitterServer = "1.19.0"
  val utilVersion = "6.33.0"

  val logback = "1.0.13"
  val spring = "3.2.15.RELEASE"
  val slf4j = "1.7.12"
  val grizzled = "1.0.2"

  val guava = "16.0.1"
  val aspectj = "1.8.2"
  val commonsIo = "2.4"
  val commonsLang = "2.6"
  val jodaTime = "2.5"
  val servletApi = "2.5"
  val druid = "1.0.23"
  val hibernateEntitymanage = "3.6.10.Final"
  val mysqlConnectorJava = "5.1.37"
  val commonsBeanutils = "1.9.2"

  val redis = "2.7.2"
  val fastjson = "1.2.6"
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
  publishArtifact in(Compile, packageDoc) := true,
  publishArtifact in(Test, packageDoc) := true,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "http://123.57.227.107:8086/nexus/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "content/repositories/releases")
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
  publishM2 := {},
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
  slf4j,
  utils,
  dtsCore,
  dtsClient
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
      "com.twitter" %% "finagle-thriftmux" % versions.finagle,
      "com.twitter" %% "finagle-serversets" % versions.finagle
    )
  ).
  dependsOn(
    inject,
    slf4j
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
    thrift,
    dtsClient
  )

lazy val utils = project.settings(rainieSettings)
  .settings(
    name := "rainie-utils",
    moduleName := "rainie-utils",
    libraryDependencies ++= Seq(
      "com.twitter" %% "util-core" % versions.utilVersion,
      "commons-lang" % "commons-lang" % versions.commonsLang
    )
  )


lazy val http = project.settings(rainieSettings)
  .settings(
    name := "rainie-http",
    moduleName := "rainie-http",
    libraryDependencies ++= Seq(
      "javax.servlet" % "servlet-api" % versions.servletApi
    )
  ).dependsOn(
    inject,
    utils
  )


lazy val dtsCore = (project in file("dts/dts-core")).
  settings(rainieSettings).
  settings(
    name := "dts-core",
    moduleName := "dts-core",
    libraryDependencies ++= Seq(
      "org.springframework" % "spring-beans" % versions.spring,
      "org.springframework" % "spring-orm" % versions.spring,
      "org.springframework" % "spring-context" % versions.spring,
      "com.alibaba" % "druid" % versions.druid,
      "com.twitter" %% "finagle-core" % versions.finagle,
      "com.twitter" %% "finagle-stats" % versions.finagle,

      "org.springframework" % "spring-test" % versions.spring % "test",
      "mysql" % "mysql-connector-java" % versions.mysqlConnectorJava % "test"

    )
  ).dependsOn(
    inject,
    slf4j
  )

lazy val dtsClient = (project in file("dts/dts-client")).
  settings(rainieSettings).
  settings(
    name := "dts-client",
    moduleName := "dts-client",
    libraryDependencies ++= Seq(
      "com.google.guava" % "guava" % versions.guava,
      "commons-beanutils" % "commons-beanutils" % versions.commonsBeanutils,
      "org.aspectj" % "aspectjweaver" % versions.aspectj,
      "org.springframework" % "spring-test" % versions.spring % "test"
    )
  ).
  dependsOn(
    dtsCore,
    http
  )

lazy val dtsServer = (project in file("dts/dts-server")).
  settings(rainieSettings).
  settings(
    name := "dts-server",
    moduleName := "dts-server",
    libraryDependencies ++= Seq(
      "commons-lang" % "commons-lang" % versions.commonsLang,
      "mysql" % "mysql-connector-java" % versions.mysqlConnectorJava,
      "org.aspectj" % "aspectjweaver" % versions.aspectj,
      "org.springframework" % "spring-test" % versions.spring  % "test"
    ),
    excludeDependencies ++= Seq(
      "org.sonatype.sisu.inject" % "cglib",
      "org.slf4j" % "slf4j-log4j12",
      "org.slf4j" % "slf4j-jdk14"
    )
  ).
  dependsOn(
    thrift,
    dtsCore
  )
  .settings(
    mainClass in assembly := Some("com.itiancai.galaxy.dts.server.DTSServer"),
    assemblyMergeStrategy in assembly := {
      case x if Assembly.isConfigFile(x) =>
        MergeStrategy.concat
      case PathList(ps@_*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
        MergeStrategy.rename
      case PathList("org", "apache", "commons", "logging", xs@_*) => MergeStrategy.first
      case PathList("org", "aspectj", "internal", "lang", xs@_*) => MergeStrategy.first
      case PathList("org", "aspectj", "runtime", "reflect", xs@_*) => MergeStrategy.first
      case PathList("META-INF", xs@_*) =>
        (xs map {
          _.toLowerCase
        }) match {
          case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
            MergeStrategy.discard
          case ps@(x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
            MergeStrategy.discard
          case "plexus" :: xs =>
            MergeStrategy.discard
          case "spring.tooling" :: xs =>
            MergeStrategy.discard
          case "services" :: xs =>
            MergeStrategy.filterDistinctLines
          case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
            MergeStrategy.filterDistinctLines
          case _ => MergeStrategy.deduplicate
        }
      case "asm-license.txt" | "overview.html" | "changelog.txt" | "com/twitter/common/args/apt/cmdline.arg.info.txt.1" =>
        MergeStrategy.discard
      case _ => MergeStrategy.deduplicate
    }
  )

lazy val dtsClientDemo = (project in file("examples/dts-demo/dts-client-demo")).
  settings(rainieSettings).
  settings(
    name := "dts-client-demo",
    moduleName := "dts-client-demo",
    libraryDependencies ++= Seq(
      "mysql" % "mysql-connector-java" % versions.mysqlConnectorJava
    )
  ).
  dependsOn(
    dtsClient,
    dtsClientDemoIdl,
    dtsClientActionDemoIdl
  )
lazy val dtsClientDemoIdl = (project in file("examples/dts-demo/dts-client-demo-idl")).
  settings(rainieSettings).
  settings(
    name := "dts-client-demo-idl",
    moduleName := "dts-client-demo-idl",
    scroogeThriftSourceFolder in Compile <<= baseDirectory {
      base => base / "thrift"
    }
  ).
  dependsOn(
    thrift
  )


lazy val dtsClientActionDemo = (project in file("examples/dts-demo/dts-client-action-demo")).
  settings(rainieSettings).
  settings(
    name := "dts-client-action-demo",
    moduleName := "dts-client-action-demo",
    libraryDependencies ++= Seq(
      "mysql" % "mysql-connector-java" % versions.mysqlConnectorJava
    )
  ).
  dependsOn(
    dtsClient,
    dtsClientActionDemoIdl
  )
lazy val dtsClientActionDemoIdl = (project in file("examples/dts-demo/dts-client-action-demo-idl")).
  settings(rainieSettings).
  settings(
    name := "dts-client-action-demo-idl",
    moduleName := "dts-client-action-demo-idl",
    scroogeThriftSourceFolder in Compile <<= baseDirectory {
      base => base / "thrift"
    }
  ).
  dependsOn(
    thrift
  )