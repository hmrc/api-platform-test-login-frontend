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

package uk.gov.hmrc.api.testlogin.connectors

import javax.inject.Singleton

import play.api.http.{Status, HeaderNames}
import uk.gov.hmrc.api.testlogin.config.WSHttp
import uk.gov.hmrc.api.testlogin.models.JsonFormatters._
import uk.gov.hmrc.api.testlogin.models._
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{Upstream4xxResponse, HeaderCarrier}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ApiPlatformTestUserConnector extends ServicesConfig {
  lazy val serviceUrl: String = baseUrl("api-platform-test-user")

  def authenticate(loginRequest: LoginRequest)(implicit hc:HeaderCarrier): Future[AuthenticatedSession] = {
    WSHttp.POST(s"$serviceUrl/session", loginRequest) map { response =>
        val authenticationResponse = response.json.as[AuthenticationResponse]

        (response.header(HeaderNames.AUTHORIZATION), response.header(HeaderNames.LOCATION)) match {
          case (Some(authBearerToken), Some(authorityUri)) =>
            AuthenticatedSession(
              authBearerToken,
              authorityUri,
              authenticationResponse.gatewayToken,
              authenticationResponse.affinityGroup)
          case _ => throw new RuntimeException("Authorization and Location headers must be present in response")
      }
    } recoverWith {
      case e: Upstream4xxResponse if e.upstreamResponseCode == Status.UNAUTHORIZED => Future.failed(LoginFailed(loginRequest.userId))
    }
  }
}
