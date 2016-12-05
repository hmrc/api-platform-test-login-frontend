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

package uk.gov.hmrc.api.testlogin.services

import javax.inject.Inject

import uk.gov.hmrc.api.testlogin.connectors.{ApiPlatformTestUserConnector, ApiPlatformTestUserConnectorImpl, AuthLoginStubConnector, AuthLoginStubConnectorImpl}
import uk.gov.hmrc.api.testlogin.models.LoginRequest
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait LoginService {
  val apiPlatformTestUserConnector: ApiPlatformTestUserConnector
  val authLoginStubConnector: AuthLoginStubConnector

  def authenticate(loginRequest: LoginRequest)(implicit hc: HeaderCarrier): Future[String] = {
    for {
      user <- apiPlatformTestUserConnector.authenticate(loginRequest)
      session <- authLoginStubConnector.createSession(user)
    } yield session
  }
}

class LoginServiceImpl @Inject()(override val apiPlatformTestUserConnector: ApiPlatformTestUserConnectorImpl,
                                 override val authLoginStubConnector: AuthLoginStubConnectorImpl) extends LoginService
