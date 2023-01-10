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

package uk.gov.hmrc.api.testlogin.stubs

import com.github.tomakehurst.wiremock.client.WireMock._

import play.api.http.HeaderNames
import play.api.http.Status.{CREATED, UNAUTHORIZED}
import uk.gov.hmrc.api.testlogin.helpers.WireMockJsonSugar
import uk.gov.hmrc.api.testlogin.models.JsonFormatters._
import uk.gov.hmrc.api.testlogin.models.{AuthenticatedSession, AuthenticationResponse, LoginRequest}

object ApiPlatformTestUserStub extends WireMockJsonSugar {

  def givenAuthenticationWillSucceedWith(loginRequest: LoginRequest, authenticatedSession: AuthenticatedSession) = {
    stubFor(
      post(urlPathEqualTo("/session"))
      .withJsonRequestBody(loginRequest)
      .willReturn(
        aResponse()
        .withStatus(CREATED)
        .withJsonBody(AuthenticationResponse(authenticatedSession.gatewayToken, authenticatedSession.affinityGroup))
        .withHeader(HeaderNames.AUTHORIZATION, authenticatedSession.authBearerToken)
        .withHeader(HeaderNames.LOCATION, authenticatedSession.authorityURI)
      )
    )
  }

  def willFailAuthenticationByDefault() = {
    stubFor(
      post(urlPathEqualTo("/session"))
      .willReturn(aResponse().withStatus(UNAUTHORIZED))
    )
  }
}
