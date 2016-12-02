/*
 * Copyright 2016 HM Revenue & Customs
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

package unit.uk.gov.hmrc.testlogin.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.libs.ws.WSClient
import uk.gov.hmrc.api.testlogin.connectors.AuthLoginStubConnector
import uk.gov.hmrc.api.testlogin.models.{TestOrganisation, TestIndividual}
import uk.gov.hmrc.domain._
import uk.gov.hmrc.play.test.{WithFakeApplication, UnitSpec}

class AuthLoginStubConnectorSpec extends UnitSpec with WiremockSugar with WithFakeApplication {

  val individual = TestIndividual("543212311772", SaUtr("1097172564"), Nino("AA100010B"))
  val organisation = TestOrganisation("543212311773", SaUtr("1097172565"), EmpRef("904", "HZ00067"), CtUtr("543212311774"), Vrn("999981316"))

  trait Setup {
    val underTest = new AuthLoginStubConnector {
      override val serviceUrl: String = wireMockUrl
      override val wsClient: WSClient = fakeApplication.injector.instanceOf[WSClient]
    }
    val decryptedSession = "loggedInUser=user1"
    val encryptedSession = "[E2rl7NxTtLvOrr+xjMP1e8SPSXgWyEbNAYSXcpjYVOic3fWPe00qqL91KlB0YI+sGH4=]"
  }

  "createSession" should {
    "return a logged in session for an individual" in new Setup {

      stubFor(post(urlEqualTo("/auth-login-stub/gg-sign-in"))
        .withRequestBody(containing("authorityId=543212311772"))
        .withRequestBody(containing("affinityGroup=Individual"))
        .withRequestBody(containing("nino=AA100010B"))
        .withRequestBody(containing("confidenceLevel=300"))
        .withRequestBody(containing("credentialStrength=strong"))
        .withRequestBody(containing("enrolment[0].name=sa"))
        .withRequestBody(containing("enrolment[0].value=1097172564"))
        .withRequestBody(containing("enrolment[0].state=Activated"))
        .willReturn(aResponse().withStatus(303).withHeader("Set-Cookie", s"mdtp=$encryptedSession")))

      val result = await(underTest.createSession(individual))

      result shouldBe s"mdtp=$decryptedSession"
    }

    "return a logged in session for an organisation" in new Setup {

      stubFor(post(urlEqualTo("/auth-login-stub/gg-sign-in"))
        .withRequestBody(containing("authorityId=543212311773"))
        .withRequestBody(containing("affinityGroup=Organisation"))
        .withRequestBody(containing("confidenceLevel=300"))
        .withRequestBody(containing("credentialStrength=strong"))
        .withRequestBody(containing("enrolment[0].name=sa"))
        .withRequestBody(containing("enrolment[0].value=1097172565"))
        .withRequestBody(containing("enrolment[0].state=Activated"))
        .withRequestBody(containing("enrolment[1].name=ct"))
        .withRequestBody(containing("enrolment[1].value=543212311774"))
        .withRequestBody(containing("enrolment[1].state=Activated"))
        .withRequestBody(containing("enrolment[2].name=vat"))
        .withRequestBody(containing("enrolment[2].value=999981316"))
        .withRequestBody(containing("enrolment[2].state=Activated"))
        .withRequestBody(containing("enrolment[3].name=epaye"))
        .withRequestBody(containing("enrolment[3].value=904%2FHZ00067"))
        .withRequestBody(containing("enrolment[3].state=Activated"))
        .willReturn(aResponse().withStatus(303).withHeader("Set-Cookie", s"mdtp=$encryptedSession")))

      val result = await(underTest.createSession(organisation))

      result shouldBe s"mdtp=$decryptedSession"
    }

    "fails when auth-login-stubs returns an error" in new Setup {

      stubFor(post(urlEqualTo("/auth-login-stub/gg-sign-in"))
        .willReturn(aResponse().withStatus(500).withHeader("Set-Cookie", s"mdtp=$encryptedSession")))

      intercept[RuntimeException]{await(underTest.createSession(organisation))}
    }

    "fails when auth-login-stubs does not return the cookie" in new Setup {

      stubFor(post(urlEqualTo("/auth-login-stub/gg-sign-in"))
        .willReturn(aResponse().withStatus(303)))

      intercept[RuntimeException]{await(underTest.createSession(organisation))}
    }

  }
}
