import scoverage.ScoverageKeys

object ScoverageSettings {
  def apply() = Seq(
    ScoverageKeys.coverageMinimumStmtTotal := 93,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    ScoverageKeys.coverageExcludedPackages := 
      "<empty>;"+
      ".*definition.*;" +
      "prod.*;" +
      "testOnlyDoNotUseInAppConf.*;" +
      "app.*;" +
      "uk.gov.hmrc.BuildInfo;"+
      ".*Reverse.*;" +
      ".*.javascript;"
  )
}
