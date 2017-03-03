/*
 * Copyright 2017 HM Revenue & Customs
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

package it.uk.gov.hmrc.api.testlogin

import it.uk.gov.hmrc.api.testlogin.pages.{ContinuePage, LoginPage}
import it.uk.gov.hmrc.api.testlogin.stubs.ApiPlatformTestUserStub.givenIndividualHasPassword
import it.uk.gov.hmrc.api.testlogin.stubs.AuthLoginStub.givenNextAuthSessionReturnedForUserIs
import it.uk.gov.hmrc.api.testlogin.stubs.ContinuePageStub
import uk.gov.hmrc.api.testlogin.models.TestIndividual
import uk.gov.hmrc.crypto.ApplicationCrypto.SessionCookieCrypto
import uk.gov.hmrc.crypto.Crypted
import uk.gov.hmrc.domain._

import scala.collection.JavaConversions._


class LoginSpec extends BaseSpec {

  val testUser = TestIndividual("543212311772", SaUtr("1097172564"), Nino("AA100010B"))
  val password = "validPassword"
  val authSession = "authToken=Bearer+1234"

  feature("User Login") {

    scenario("Successful login") {

      Given("A test user")
      givenIndividualHasPassword(testUser, password)

      And("Auth-login-stub returns a session for the user")
      givenNextAuthSessionReturnedForUserIs(testUser.username, authSession)

      When("I login with the user's credentials")
      goOn(LoginPage)
      textField("userId").value = testUser.username
      pwdField("password").value = password
      clickOnSubmit()

      Then("I am redirected to the continue URL")
      on(ContinuePage)

      And("The cookie is set in the session")
      val encryptedMdtpCookie = webDriver.manage().getCookies.toSet.find(_.getName == "mdtp").get
      val mdtpCookie = SessionCookieCrypto.decrypt(Crypted(encryptedMdtpCookie.getValue)).value
      mdtpCookie shouldBe authSession
    }

    scenario("Failed login") {

      Given("A test user")
      givenIndividualHasPassword(testUser, password)

      When("I try to login with the wrong password")
      goOn(LoginPage)
      textField("userId").value = testUser.username
      pwdField("password").value = "wrongPassword"
      clickOnSubmit()

      Then("I am on the login page")
      on(LoginPage)

      And("An error message is displayed")
      verifyText("error-notification", "Invalid user ID or password. Try again.")
    }
  }

  override protected def beforeEach() = {
    super.beforeEach()
    ContinuePageStub.givenContinuePageIsUp()
  }
}
