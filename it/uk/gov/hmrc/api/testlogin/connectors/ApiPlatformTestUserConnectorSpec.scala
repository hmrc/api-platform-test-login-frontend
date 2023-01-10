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

package uk.gov.hmrc.api.testlogin.connectors

import scala.concurrent.ExecutionContext.Implicits.global

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.http.HeaderNames.{AUTHORIZATION, LOCATION}
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.{Application, Environment}
import uk.gov.hmrc.api.testlogin.AsyncHmrcSpec
import uk.gov.hmrc.api.testlogin.config.AppConfig
import uk.gov.hmrc.api.testlogin.helpers.WireMockSugar
import uk.gov.hmrc.api.testlogin.models.JsonFormatters._
import uk.gov.hmrc.api.testlogin.models.{AuthenticatedSession, LoginFailed, LoginRequest}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, UpstreamErrorResponse}

class ApiPlatformTestUserConnectorSpec extends AsyncHmrcSpec with WireMockSugar with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(
        "auditing.enabled"                                  -> false,
        "auditing.traceRequests"                            -> false,
        "microservice.services.api-platform-test-user.port" -> stubPort,
        "metrics.jvm"                                       -> false
      )
      .build()

  trait Setup {
    implicit val hc = HeaderCarrier()

    val appConfig = mock[AppConfig]

    val underTest = new ApiPlatformTestUserConnector(
      fakeApplication.injector.instanceOf[HttpClient],
      appConfig,
      fakeApplication.injector.instanceOf[Environment]
    )

    when(appConfig.serviceUrl).thenReturn(wireMockUrl)
  }

  val loginRequest = LoginRequest("user", "password")

  "authenticate" should {
    "return the auth session when the credentials are valid" in new Setup {
      val authBearerToken = "Bearer AUTH_TOKEN"
      val userOid         = "/auth/oid/12345"
      val gatewayToken    = "GG_TOKEN"
      val affinityGroup   = "Individual"

      stubFor(
        post(urlEqualTo("/session"))
          .withJsonRequestBody(loginRequest)
          .willReturn(aResponse()
            .withStatus(CREATED)
            .withBody(Json.obj("gatewayToken" -> gatewayToken, "affinityGroup" -> affinityGroup).toString())
            .withHeader(AUTHORIZATION, authBearerToken)
            .withHeader(LOCATION, userOid))
      )

      val result = await(underTest.authenticate(loginRequest))

      result shouldBe AuthenticatedSession(authBearerToken, userOid, gatewayToken, affinityGroup)
    }

    "fail with LoginFailed when the credentials are not valid" in new Setup {
      stubFor(post(urlEqualTo("/session"))
        .withRequestBody(equalToJson(toJson(loginRequest).toString()))
        .willReturn(aResponse()
          .withStatus(UNAUTHORIZED)))

      intercept[LoginFailed] {
        await(underTest.authenticate(loginRequest))
      }
    }

    "fail when the authenticate call returns an error" in new Setup {
      stubFor(
        post(urlEqualTo("/session"))
          .withJsonRequestBody(loginRequest)
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      intercept[UpstreamErrorResponse] {
        await(underTest.authenticate(loginRequest))
      }.statusCode shouldBe INTERNAL_SERVER_ERROR
    }

  }
}
