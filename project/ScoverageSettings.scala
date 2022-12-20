import scoverage.ScoverageKeys

object ScoverageSettings {
  def apply() = Seq(
    ScoverageKeys.coverageMinimumStmtTotal := 94,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    ScoverageKeys.coverageExcludedPackages := 
      "<empty>;"+
      "com.kenshoo.play.metrics.*;" +
      ".*definition.*;" +
      "prod.*;" +
      "testOnlyDoNotUseInAppConf.*;" +
      "app.*;" +
      "uk.gov.hmrc.BuildInfo;"+
      ".*Reverse.*;" +
      ".*.javascript;"
  )
}
