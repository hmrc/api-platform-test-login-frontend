import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport._

object AppDependencies {
  def apply() = compile ++ test

  private lazy val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % "5.16.0",
    "uk.gov.hmrc"             %% "domain"                     % "6.2.0-play-28",
    "uk.gov.hmrc"             %% "play-partials"              % "8.2.0-play-28",
    "uk.gov.hmrc"             %% "play-json-union-formatter"  % "1.15.0-play-28",
    "uk.gov.hmrc"             %% "govuk-template"             % "5.70.0-play-28",
    "uk.gov.hmrc"             %% "play-frontend-govuk"        % "1.0.0-play-28",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "1.6.0-play-28"
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % "5.16.0",
    "org.jsoup"               %  "jsoup"                      % "1.8.1",
    "com.typesafe.play"       %% "play-test"                  % PlayVersion.current,
    "com.github.tomakehurst"  %  "wiremock-jre8-standalone"   % "2.31.0",
    "org.seleniumhq.selenium" %  "selenium-java"              % "3.141.59",
    "org.seleniumhq.selenium" %  "selenium-firefox-driver"    % "3.141.59",
    "org.seleniumhq.selenium" %  "selenium-chrome-driver"     % "3.141.59",
    "org.mockito"             %% "mockito-scala-scalatest"    % "1.7.1"
  ).map(_ % "test, it")
}
