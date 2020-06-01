import _root_.play.sbt.routes.RoutesKeys.routesGenerator
import play.core.PlayVersion
import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import uk.gov.hmrc.versioning.SbtGitVersioning

import scala.util.Properties

lazy val appName = "api-platform-test-login-frontend"
lazy val appDependencies: Seq[ModuleID] = compile ++ test

lazy val bootstrapPlayVersion = "1.5.0"
lazy val playPartialsVersion = "6.9.0-play-26"
lazy val hmrcTestVersion = "3.9.0-play-26"
lazy val scalaTestVersion = "3.0.8"
lazy val pegdownVersion = "1.6.0"
lazy val scalaTestPlusVersion = "3.1.3"
lazy val wiremockVersion = "1.58"
lazy val hmrcPlayJsonUnionFormatterVersion = "1.5.0"
lazy val mockitoVersion = "1.10.19"
lazy val govUkTemplateVersion = "5.52.0-play-26"
lazy val playUiVersion = "8.8.0-play-26"
lazy val domainVersion = "5.6.0-play-26"

lazy val compile = Seq(
  "uk.gov.hmrc" %% "bootstrap-play-26" % bootstrapPlayVersion,
  "uk.gov.hmrc" %% "domain" % domainVersion,
  "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
  "uk.gov.hmrc" %% "play-json-union-formatter" % hmrcPlayJsonUnionFormatterVersion,
  "uk.gov.hmrc" %% "govuk-template" % govUkTemplateVersion,
  "uk.gov.hmrc" %% "play-ui" % playUiVersion
)

lazy val scope: String = "test, it"

lazy val test = Seq(
  "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
  "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % scope,
  "org.pegdown" % "pegdown" % pegdownVersion % scope,
  "org.jsoup" % "jsoup" % "1.8.1" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.mockito" % "mockito-core" % mockitoVersion % scope,
  "com.github.tomakehurst" % "wiremock" % wiremockVersion % scope,
  "org.seleniumhq.selenium" % "selenium-java" % "2.53.1" % scope,
  "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0"
)

lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

def unitFilter(name: String): Boolean = name startsWith "unit"
def itTestFilter(name: String): Boolean = name startsWith "it"

lazy val microservice = (project in file("."))
  .enablePlugins(Seq(_root_.play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory) ++ plugins: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    name := appName,
    scalaVersion := "2.11.12",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    parallelExecution in Test := false,
    fork in Test := false,
    testOptions in Test := Seq(Tests.Filter(unitFilter)),
    majorVersion := 0
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    fork in IntegrationTest := false,
    testOptions in IntegrationTest := Seq(Tests.Filter(itTestFilter)),
    unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest) (base => Seq(base / "test")),
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false)
  .settings(resolvers ++= Seq(
    Resolver.bintrayRepo("hmrc", "releases"),
    Resolver.jcenterRepo
  ))

def oneForkedJvmPerTest(tests: Seq[TestDefinition]) =
  tests map {
    test =>
      Group(
        test.name,
        Seq(test),
        SubProcess(
          ForkOptions(
            runJVMOptions = Seq(
              s"-Dtest.name=${test.name}",
              s"-Dtest_driver=${Properties.propOrElse("test_driver", "chrome")}"))))
  }

// Coverage configuration
coverageMinimum := 96
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo;.*javascript;.*Reverse.*"
