import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport._

object AppDependencies {
  def apply() = compile ++ test

  private lazy val bootstrapPlayVersion = "5.10.0"
  private lazy val playPartialsVersion = "6.11.0-play-26"
  private lazy val hmrcPlayJsonUnionFormatterVersion = "1.14.0-play-26"
  private lazy val govUkTemplateVersion = "5.69.0-play-26"
  private lazy val domainVersion = "6.1.0-play-26"

  private lazy val scalaTestPlusVersion = "3.1.3"
  private lazy val pegdownVersion = "1.6.0"
  private lazy val wiremockVersion = "2.27.1"

  private lazy val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-26" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-json-union-formatter" % hmrcPlayJsonUnionFormatterVersion,
    "uk.gov.hmrc" %% "govuk-template" % govUkTemplateVersion,
    "uk.gov.hmrc" %% "play-frontend-govuk" % "0.63.0-play-26",
    "uk.gov.hmrc" %% "play-frontend-hmrc" % "0.57.0-play-26"
  )

  private lazy val scope: String = "test, it"

  private lazy val test = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % scope,
    "org.pegdown" % "pegdown" % pegdownVersion % scope,
    "org.jsoup" % "jsoup" % "1.8.1" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "com.github.tomakehurst" % "wiremock-jre8-standalone" % wiremockVersion % scope,
    "org.seleniumhq.selenium" % "selenium-java" % "3.141.59" % scope,
    "org.seleniumhq.selenium" % "selenium-firefox-driver" % "3.141.59" % scope,
    "org.seleniumhq.selenium" % "selenium-chrome-driver" % "3.141.59" % scope,
    "org.mockito" %% "mockito-scala-scalatest" % "1.7.1" % scope
  )
}