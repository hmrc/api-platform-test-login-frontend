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

package uk.gov.hmrc.api.testlogin.connectors

import javax.inject.{Inject, Singleton}
import play.api.http.{HeaderNames, Status}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.api.testlogin.models.JsonFormatters._
import uk.gov.hmrc.api.testlogin.models._
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiPlatformTestUserConnector @Inject()(httpClient: HttpClient,
                                             override val runModeConfiguration: Configuration,
                                             environment: Environment
                                            )(implicit ec: ExecutionContext) extends ServicesConfig {

  override protected def mode = environment.mode

  lazy val serviceUrl: String = baseUrl("api-platform-test-user")

  def authenticate(loginRequest: LoginRequest)(implicit hc: HeaderCarrier): Future[AuthenticatedSession] = {
    httpClient.POST(s"$serviceUrl/session", loginRequest) map { response =>
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
      case e: Upstream4xxResponse if e.upstreamResponseCode == Status.UNAUTHORIZED => Future.failed(LoginFailed(loginRequest.username))
    }
  }
}
