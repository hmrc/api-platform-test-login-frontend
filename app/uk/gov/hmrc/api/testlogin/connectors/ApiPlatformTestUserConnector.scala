/*
 * Copyright 2021 HM Revenue & Customs
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
import scala.concurrent.{ExecutionContext, Future}

import play.api.Environment
import play.api.http.{HeaderNames, Status}
import uk.gov.hmrc.api.testlogin.config.AppConfig
import uk.gov.hmrc.api.testlogin.models.JsonFormatters._
import uk.gov.hmrc.api.testlogin.models._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}
import uk.gov.hmrc.http.HttpClient

@Singleton
class ApiPlatformTestUserConnector @Inject()(httpClient: HttpClient,
                                             appConfig: AppConfig,
                                             environment: Environment
                                            )(implicit ec: ExecutionContext) {

  import appConfig.serviceUrl

 def authenticate(loginRequest: LoginRequest)(implicit hc: HeaderCarrier): Future[AuthenticatedSession] = {
    httpClient.POST[LoginRequest, Either[UpstreamErrorResponse, HttpResponse]](s"$serviceUrl/session", loginRequest) map {
      case Right(response) => 
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

      case Left(UpstreamErrorResponse(_,Status.UNAUTHORIZED, _, _)) => throw LoginFailed(loginRequest.username)
      case Left(err) => throw err
    }
  }
}
