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
import play.api.libs.json.Json._
import uk.gov.hmrc.api.testlogin.config.WSHttp
import uk.gov.hmrc.api.testlogin.connectors.ApiPlatformTestUserConnector
import uk.gov.hmrc.api.testlogin.models.{LoginFailed, LoginRequest, TestOrganisation, TestIndividual}
import uk.gov.hmrc.api.testlogin.models.JsonFormatters._
import uk.gov.hmrc.domain._
import uk.gov.hmrc.play.http.{UnauthorizedException, HeaderCarrier, HttpPost}
import uk.gov.hmrc.play.test.{WithFakeApplication, UnitSpec}

class ApiPlatformTestUserConnectorSpec extends UnitSpec with WiremockSugar with WithFakeApplication {

  val individual = TestIndividual("543212311772", SaUtr("1097172564"), Nino("AA100010B"))
  val organisation = TestOrganisation("543212311773", SaUtr("1097172565"), EmpRef("904", "HZ00067"), CtUtr("543212311774"), Vrn("999981316"))

  trait Setup {
    implicit val hc = HeaderCarrier()
    val underTest = new ApiPlatformTestUserConnector {
      override val http: HttpPost = WSHttp
      override val serviceUrl: String = wireMockUrl
    }
  }

  "authenticate" should {

    "return the test individual when the credentials are valid for an individual" in new Setup {

      val loginRequest = LoginRequest(individual.username, "validPassword")

      stubFor(post(urlEqualTo("/authenticate"))
        .withRequestBody(equalToJson(toJson(loginRequest).toString()))
        .willReturn(aResponse().withStatus(200).withBody(
          s"""
            |{
            |   "username": "${individual.username}",
            |   "saUtr": "${individual.saUtr}",
            |   "nino": "${individual.nino}",
            |   "userType" : "INDIVIDUAL"
            |}
          """.stripMargin)))

      val result = await(underTest.authenticate(loginRequest))

      result shouldBe individual
    }

    "return the test organisation when the credentials are valid for an organisation" in new Setup {

      val loginRequest = LoginRequest(organisation.username, "validPassword")

      stubFor(post(urlEqualTo("/authenticate"))
        .withRequestBody(equalToJson(toJson(loginRequest).toString()))
        .willReturn(aResponse().withStatus(200).withBody(
          s"""
             |{
             |   "username": "${organisation.username}",
             |   "saUtr": "${organisation.saUtr}",
             |   "ctUtr": "${organisation.ctUtr}",
             |   "empRef": "${organisation.empRef}",
             |   "vrn": "${organisation.vrn}",
             |   "userType" : "ORGANISATION"
             |}
          """.stripMargin)))

      val result = await(underTest.authenticate(loginRequest))

      result shouldBe organisation
    }

    "fail with LoginFailed when the credentials are not valid" in new Setup {

      val invalidLoginRequest = LoginRequest(organisation.username, "invalidPassword")

      stubFor(post(urlEqualTo("/authenticate"))
        .withRequestBody(equalToJson(toJson(invalidLoginRequest).toString()))
        .willReturn(aResponse().withStatus(401)))

      intercept[LoginFailed]{await(underTest.authenticate(invalidLoginRequest))}
    }

    "fail when the authenticate call returns an error" in new Setup {

      val invalidLoginRequest = LoginRequest(organisation.username, "invalidPassword")

      stubFor(post(urlEqualTo("/authenticate"))
        .withRequestBody(equalToJson(toJson(invalidLoginRequest).toString()))
        .willReturn(aResponse().withStatus(500)))

      intercept[RuntimeException]{await(underTest.authenticate(invalidLoginRequest))}
    }

  }
}
