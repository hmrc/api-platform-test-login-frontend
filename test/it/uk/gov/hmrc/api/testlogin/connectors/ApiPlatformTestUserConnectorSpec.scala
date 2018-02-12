/*
 * Copyright 2018 HM Revenue & Customs
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

package it.uk.gov.hmrc.api.testlogin.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import it.uk.gov.hmrc.api.testlogin.helpers.WiremockSugar
import play.api.http.HeaderNames.{AUTHORIZATION, LOCATION}
import play.api.libs.json.Json
import play.api.libs.json.Json._
import uk.gov.hmrc.api.testlogin.connectors.ApiPlatformTestUserConnector
import uk.gov.hmrc.api.testlogin.models.{AuthenticatedSession, LoginFailed, LoginRequest}
import uk.gov.hmrc.play.test.{WithFakeApplication, UnitSpec}
import uk.gov.hmrc.api.testlogin.models.JsonFormatters._
import uk.gov.hmrc.http.{ HeaderCarrier, Upstream5xxResponse }

class ApiPlatformTestUserConnectorSpec extends UnitSpec with WiremockSugar with WithFakeApplication {

  trait Setup {
    implicit val hc = HeaderCarrier()
    val underTest = new ApiPlatformTestUserConnector {
      override lazy val serviceUrl: String = wireMockUrl
    }
  }

  val loginRequest = LoginRequest("user", "password")

  "authenticate" should {

    "return the auth session when the credentials are valid" in new Setup {
      val authBearerToken = "Bearer AUTH_TOKEN"
      val userOid = "/auth/oid/12345"
      val gatewayToken = "GG_TOKEN"
      val affinityGroup = "Individual"

      stubFor(post(urlEqualTo("/session"))
        .withRequestBody(equalToJson(toJson(loginRequest).toString()))
        .willReturn(aResponse()
          .withStatus(201)
          .withBody(Json.obj("gatewayToken" -> gatewayToken, "affinityGroup" -> affinityGroup).toString())
          .withHeader(AUTHORIZATION, authBearerToken)
          .withHeader(LOCATION, userOid)))

      val result = await(underTest.authenticate(loginRequest))

      result shouldBe AuthenticatedSession(authBearerToken, userOid, gatewayToken, affinityGroup)
    }

    "fail with LoginFailed when the credentials are not valid" in new Setup {

      stubFor(post(urlEqualTo("/session"))
        .withRequestBody(equalToJson(toJson(loginRequest).toString()))
        .willReturn(aResponse()
          .withStatus(401)))

      intercept[LoginFailed]{await(underTest.authenticate(loginRequest))}
    }

    "fail when the authenticate call returns an error" in new Setup {

      stubFor(post(urlEqualTo("/session"))
        .withRequestBody(equalToJson(toJson(loginRequest).toString()))
        .willReturn(aResponse()
          .withStatus(500)))

      intercept[Upstream5xxResponse]{await(underTest.authenticate(loginRequest))}
    }

  }
}
