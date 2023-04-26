import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport._


object AppDependencies {
  val seleniumVersion = "4.8.3"

  def apply() = compile ++ test

  lazy val bootstrapVersion  = "7.12.0"

  private lazy val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-partials"              % "8.3.0-play-28",
    "uk.gov.hmrc"             %% "domain"                     % "8.1.0-play-28",
    "uk.gov.hmrc"             %% "play-json-union-formatter"  % "1.17.0-play-28",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "5.3.0-play-28"
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion,
    "org.jsoup"               %  "jsoup"                      % "1.8.1",
    "com.typesafe.play"       %% "play-test"                  % PlayVersion.current,
    "com.github.tomakehurst"  %  "wiremock-jre8-standalone"   % "2.31.0",
    "uk.gov.hmrc"             %% "webdriver-factory"          % "0.39.0",
    "org.scalatestplus"       %% "selenium-4-7"               % "3.2.15.0",
    "org.seleniumhq.selenium" %  "selenium-java"              % seleniumVersion,
    "org.seleniumhq.selenium" %  "htmlunit-driver"            % seleniumVersion,
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.62.2",
    "org.mockito"             %% "mockito-scala-scalatest"    % "1.7.1"
  ).map(_ % "test, it")
}
