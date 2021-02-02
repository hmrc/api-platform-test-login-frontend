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

package uk.gov.hmrc.api.testlogin.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import uk.gov.hmrc.api.testlogin.helpers.MockHost
import play.api.http.HeaderNames
import play.api.http.Status.{CREATED, UNAUTHORIZED}
import uk.gov.hmrc.api.testlogin.models.{LoginRequest, AuthenticationResponse, AuthenticatedSession}
import uk.gov.hmrc.api.testlogin.models.JsonFormatters._
import uk.gov.hmrc.api.testlogin.helpers.WireMockJsonSugar

object ApiPlatformTestUserStub extends MockHost(11111) with WireMockJsonSugar {

  def willSucceedAuthenticationWith(loginRequest: LoginRequest, authenticatedSession: AuthenticatedSession) = {
    mock.register(
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
    mock.register(
      post(urlPathEqualTo("/session"))
      .willReturn(aResponse().withStatus(UNAUTHORIZED))
    )
  }
}
