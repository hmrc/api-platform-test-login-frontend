import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport._


object AppDependencies {
  val seleniumVersion = "4.2.2"

  def apply() = compile ++ test

  lazy val bootstrapVersion  = "5.24.0"
  private lazy val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"             %% "domain"                     % "8.1.0-play-28",
    "uk.gov.hmrc"             %% "play-partials"              % "8.3.0-play-28",
    "uk.gov.hmrc"             %% "play-json-union-formatter"  % "1.15.0-play-28",
    "uk.gov.hmrc"             %% "govuk-template"             % "5.77.0-play-28",
    "uk.gov.hmrc"             %% "play-frontend-govuk"        % "2.0.0-play-28",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "3.20.0-play-28"
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion,
    "org.jsoup"               %  "jsoup"                      % "1.8.1",
    "com.typesafe.play"       %% "play-test"                  % PlayVersion.current,
    "com.github.tomakehurst"  %  "wiremock-jre8-standalone"   % "2.31.0",
    "org.scalatestplus"       %% "selenium-4-2"               % "3.2.13.0",
    "org.seleniumhq.selenium" %  "selenium-java"              % seleniumVersion,
    "org.seleniumhq.selenium" %  "selenium-api"               % seleniumVersion,
    "org.seleniumhq.selenium" %  "selenium-firefox-driver"    % seleniumVersion,
    "org.seleniumhq.selenium" %  "selenium-chrome-driver"     % seleniumVersion,
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.62.2",
    "org.mockito"             %% "mockito-scala-scalatest"    % "1.7.1"
  ).map(_ % "test, it")
}
