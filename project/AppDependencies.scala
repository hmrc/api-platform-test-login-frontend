import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport._


object AppDependencies {
  def apply() = compile ++ test

  lazy val bootstrapVersion    = "9.13.0"
  lazy val commonDomainVersion = "0.17.0"

  private lazy val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"      % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"      % "12.6.0",
    "uk.gov.hmrc"             %% "play-partials-play-30"           % "10.1.0",
    "uk.gov.hmrc"             %% "domain-play-30"                  % "11.0.0",
    "uk.gov.hmrc"             %% "api-platform-common-domain"      % commonDomainVersion
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"              % bootstrapVersion,
    "org.jsoup"               %  "jsoup"                               % "1.15.4",
    "uk.gov.hmrc"             %% "ui-test-runner"                      % "0.33.0",
    "org.mockito"             %% "mockito-scala-scalatest"             % "1.17.30",
    "uk.gov.hmrc"             %% "api-platform-common-domain-fixtures" % commonDomainVersion
  ).map(_ % "test")
}
