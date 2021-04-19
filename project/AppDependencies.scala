import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport._

object AppDependencies {
  def apply() = compile ++ test

  lazy val bootstrapPlayVersion = "1.16.0"
  lazy val playPartialsVersion = "6.11.0-play-26"
  lazy val hmrcPlayJsonUnionFormatterVersion = "1.11.0"
  lazy val govUkTemplateVersion = "5.61.0-play-26"
  lazy val domainVersion = "5.10.0-play-26"

  lazy val mockitoVersion = "1.10.19"
  lazy val scalaTestVersion = "3.0.8"
  lazy val hmrcTestVersion = "3.9.0-play-26"
  lazy val scalaTestPlusVersion = "3.1.3"
  lazy val pegdownVersion = "1.6.0"
  lazy val wiremockVersion = "1.58"

  lazy val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-play-26" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-json-union-formatter" % hmrcPlayJsonUnionFormatterVersion,
    "uk.gov.hmrc" %% "govuk-template" % govUkTemplateVersion,
    "uk.gov.hmrc" %% "play-frontend-govuk" % "0.63.0-play-26",
    "uk.gov.hmrc" %% "play-frontend-hmrc" % "0.57.0-play-26"
  )

  lazy val scope: String = "test, it"

  lazy val test = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % scope,
    "org.pegdown" % "pegdown" % pegdownVersion % scope,
    "org.jsoup" % "jsoup" % "1.8.1" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "com.github.tomakehurst" % "wiremock" % wiremockVersion % scope,
    "org.seleniumhq.selenium" % "selenium-java" % "3.141.59" % scope,
    "org.seleniumhq.selenium" % "selenium-firefox-driver" % "3.141.59" % scope,
    "org.seleniumhq.selenium" % "selenium-chrome-driver" % "3.141.59" % scope,
    "org.mockito" %% "mockito-scala-scalatest" % "1.7.1" % scope
  )
}