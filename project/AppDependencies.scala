import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport._


object AppDependencies {
  val seleniumVersion = "4.8.3"

  def apply() = compile ++ test

  lazy val bootstrapVersion    = "8.4.0"
  lazy val commonDomainVersion = "0.13.0"

  private lazy val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"      % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"      % "8.5.0",
    "uk.gov.hmrc"             %% "play-partials-play-30"           % "9.1.0",
    "uk.gov.hmrc"             %% "domain-play-30"                  % "9.0.0",
    "uk.gov.hmrc"             %% "play-json-union-formatter"       % "1.20.0",
    "uk.gov.hmrc"             %% "api-platform-common-domain"      % commonDomainVersion
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"          % bootstrapVersion,
    "org.jsoup"               %  "jsoup"                           % "1.15.4",
    "org.mockito"             %% "mockito-scala-scalatest"         % "1.17.30",
    "uk.gov.hmrc"             %% "api-platform-test-common-domain" % commonDomainVersion
  ).map(_ % "test")
}
