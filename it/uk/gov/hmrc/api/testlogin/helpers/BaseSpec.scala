/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.api.testlogin.helpers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.openqa.selenium.WebDriver
import org.scalatest._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.api.testlogin.pages.ContinuePage
import uk.gov.hmrc.api.testlogin.stubs.{ApiPlatformTestUserStub, ContinuePageStub}

trait BaseSpec 
    extends FeatureSpec 
    with BeforeAndAfterAll 
    with BeforeAndAfterEach 
    with Matchers 
    with GuiceOneServerPerSuite
    with GivenWhenThen
    with NavigationSugar {

  override lazy val port = Env.port
  implicit val webDriver: WebDriver = Env.driver
  implicit override lazy val app: Application = GuiceApplicationBuilder()
    .configure(
      "auditing.enabled" -> false,
      "auditing.traceRequests" -> false,
      "microservice.services.api-platform-test-user.port" -> ApiPlatformTestUserStub.port,
      "continue-url" -> ContinuePage.url,
      "metrics.jvm" -> false
    )
    .build()

  val mocks = Seq(ApiPlatformTestUserStub, ContinuePageStub)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    mocks.foreach(m => if (!m.server.isRunning) m.server.start())
  }

  override protected def afterEach(): Unit = {
    webDriver.manage().deleteAllCookies()
    mocks.foreach(_.mock.resetMappings())
    super.afterEach()
  }

  override protected def afterAll(): Unit = {
    mocks.foreach(_.server.stop())
    super.afterAll()
  }
}

case class MockHost(port: Int) {
  val server = new WireMockServer(WireMockConfiguration.wireMockConfig().port(port))
  val mock = new WireMock("localhost", port)
  val url = s"http://localhost:$port"
}
