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

package uk.gov.hmrc.api.testlogin.connectors

import uk.gov.hmrc.api.testlogin.config.WSHttp
import uk.gov.hmrc.api.testlogin.models.{LoginFailed, TestUser, LoginRequest}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{UnauthorizedException, Upstream4xxResponse, HeaderCarrier, HttpPost}
import uk.gov.hmrc.api.testlogin.models.JsonFormatters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ApiPlatformTestUserConnector {

  val http: HttpPost
  val serviceUrl: String

  def authenticate(loginRequest: LoginRequest)(implicit hc:HeaderCarrier): Future[TestUser] = {
    http.POST[LoginRequest, TestUser](s"$serviceUrl/authenticate", loginRequest) recover {
      case e: Upstream4xxResponse if e.upstreamResponseCode == 401 => throw new LoginFailed(loginRequest.username)
      case e: Throwable => throw new RuntimeException(s"An unexpected error occurred while authenticating ${loginRequest.username}", e)
    }
  }
}

class ApiPlatformTestUserConnectorImpl extends ApiPlatformTestUserConnector with ServicesConfig {
  override val http: HttpPost = WSHttp
  override val serviceUrl: String = baseUrl("api-platform-test-user")
}
