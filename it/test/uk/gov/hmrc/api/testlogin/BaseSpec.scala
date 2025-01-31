/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.api.testlogin

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest._
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneServerPerTest

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.RunningServer
import play.api.{Application, Mode}
import uk.gov.hmrc.selenium.webdriver.{Browser, Driver, ScreenshotOnFailure}

import uk.gov.hmrc.api.testlogin.helpers.WebPage

trait BaseSpec
    extends AnyFeatureSpec
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with Matchers
    with GuiceOneServerPerTest
    with GivenWhenThen
    with Browser
    with ScreenshotOnFailure {

  val stubHost = "localhost"
  val stubPort = 6005

  override protected def newServerForTest(app: Application, testData: TestData): RunningServer = MyTestServerFactory.start(app)

  val wireMockServer = new WireMockServer(
    wireMockConfig().port(stubPort)
  )

  val testSpecificConfiguration: List[(String, Any)]

  override def newAppForTest(testData: TestData): Application = {
    GuiceApplicationBuilder()
      .configure(
        "auditing.enabled"                                  -> false,
        "auditing.traceRequests"                            -> false,
        "microservice.services.api-platform-test-user.port" -> stubPort,
        "metrics.jvm"                                       -> false
      )
      .configure(testSpecificConfiguration: _*)
      .in(Mode.Prod)
      .build()
  }

  override protected def beforeAll() = {
    wireMockServer.start()
    WireMock.configureFor(stubHost, stubPort)
  }

  override protected def afterAll() = {
    wireMockServer.stop()
  }

  override protected def beforeEach() = {
    startBrowser()
    Driver.instance.manage().deleteAllCookies()
    WireMock.reset()
  }

  override def afterEach(): Unit = {
    quitBrowser()
  }

  def isCurrentPage(page: WebPage): Unit = {
    page.heading shouldBe page.pageTitle
  }
}
