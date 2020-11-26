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

lazy val bootstrapPlayVersion = "1.7.0"
lazy val playPartialsVersion = "6.11.0-play-26"
lazy val hmrcTestVersion = "3.9.0-play-26"
lazy val scalaTestVersion = "3.0.8"
lazy val pegdownVersion = "1.6.0"
lazy val scalaTestPlusVersion = "3.1.3"
lazy val wiremockVersion = "1.58"
lazy val hmrcPlayJsonUnionFormatterVersion = "1.11.0"
lazy val mockitoVersion = "1.10.19"
lazy val govUkTemplateVersion = "5.54.0-play-26"
lazy val domainVersion = "5.6.0-play-26"

lazy val compile = Seq(
  "uk.gov.hmrc" %% "bootstrap-play-26" % bootstrapPlayVersion,
  "uk.gov.hmrc" %% "domain" % domainVersion,
  "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
  "uk.gov.hmrc" %% "play-json-union-formatter" % hmrcPlayJsonUnionFormatterVersion,
  "uk.gov.hmrc" %% "govuk-template" % govUkTemplateVersion,
  "uk.gov.hmrc" %% "play-frontend-govuk" % "0.49.0-play-26",
  "uk.gov.hmrc" %% "play-frontend-hmrc" % "0.26.0-play-26"
)

lazy val scope: String = "test, it"

lazy val test = Seq(
  "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % scope,
  "org.pegdown" % "pegdown" % pegdownVersion % scope,
  "org.jsoup" % "jsoup" % "1.8.1" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.mockito" % "mockito-core" % mockitoVersion % scope,
  "com.github.tomakehurst" % "wiremock" % wiremockVersion % scope,
  "org.seleniumhq.selenium" % "selenium-java" % "3.141.59" % scope,
  "org.seleniumhq.selenium" % "selenium-firefox-driver" % "3.141.59" % scope,
  "org.seleniumhq.selenium" % "selenium-chrome-driver" % "3.141.59" % scope,
)

lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

lazy val microservice = (project in file("."))
  .enablePlugins(Seq(_root_.play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory) ++ plugins: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    name := appName,
    scalaVersion := "2.12.11",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    majorVersion := 0
  )
  .settings(inConfig(Test)(Defaults.testSettings): _*)
  .settings(
    Test / testOptions := Seq(Tests.Argument(TestFrameworks.ScalaTest, "-eT")),
    Test / unmanagedSourceDirectories += baseDirectory.value / "test",
    Test / fork := false,
    Test / parallelExecution := false
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.govukfrontend.views.html.helpers._"
    )
  )
  .settings(
    IntegrationTest / fork := false,
    IntegrationTest / unmanagedSourceDirectories += baseDirectory.value / "it",
    addTestReportOption(IntegrationTest, "int-test-reports"),
    IntegrationTest / parallelExecution:= false)
  .settings(resolvers ++= Seq(
    Resolver.bintrayRepo("hmrc", "releases"),
    Resolver.jcenterRepo
  ))

// Coverage configuration
coverageMinimum := 94.5
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo;.*javascript;.*Reverse.*"
