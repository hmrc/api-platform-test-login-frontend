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

import scala.jdk.CollectionConverters._

import uk.gov.hmrc.domain._
import uk.gov.hmrc.selenium.webdriver.Driver

import uk.gov.hmrc.api.testlogin.models.{AuthenticatedSession, LoginRequest, TestIndividual}
import uk.gov.hmrc.api.testlogin.pages.{ContinuePage, LoginPage}
import uk.gov.hmrc.api.testlogin.stubs.ApiPlatformTestUserStub._
import uk.gov.hmrc.api.testlogin.stubs.{ApiPlatformTestUserStub, ContinuePageStub}

class LoginSpec extends BaseSpec {

  val testUser             = TestIndividual("543212311772", SaUtr("1097172564"), Nino("AA100010B"))
  val password             = "validPassword"
  val authenticatedSession = AuthenticatedSession("Bearer 1234", "/auth/oid/123", "GG_TOKEN", "Individual")

  lazy val continuePage = new ContinuePage(stubPort)
  lazy val loginPage    = new LoginPage(port, continuePage)

  val testSpecificConfiguration: List[(String, Any)] = List("continue-url" -> s"http://localhost:${stubPort}/continue")

  Feature("User Login") {
    Scenario("Successful login") {
      givenAuthenticationWillSucceedWith(LoginRequest(testUser.userId, password), authenticatedSession)

      When("I login with the user's credentials")
      loginPage.goTo()
      loginPage.loginWith(testUser.userId, password)

      Then("I am redirected to the continue URL")
      isCurrentPage(continuePage)

      And("The cookie is set in the session")
      val encryptedMdtpCookie = Driver.instance.manage().getCookies.asScala.toSet.find(_.getName == "mdtp")
      encryptedMdtpCookie should be(Symbol("defined"))
    }

    Scenario("Failed login") {
      When("I try to login with the wrong userId or password")
      loginPage.goTo()
      loginPage.loginWith(testUser.userId, "wrongPassword")

      Then("I am on the login page")
      isCurrentPage(loginPage)

      And("An error message is displayed")
      loginPage.errorText() shouldBe "Error:Invalid user ID or password. Try again."
    }
  }

  override protected def beforeEach() = {
    super.beforeEach()
    ContinuePageStub.whenContinuePageIsUp()
    ApiPlatformTestUserStub.willFailAuthenticationByDefault()
  }
}
