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

package unit.uk.gov.hmrc.testlogin.services


import org.mockito.BDDMockito.given
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.api.testlogin.connectors.{ApiPlatformTestUserConnector, AuthLoginStubConnector}
import uk.gov.hmrc.api.testlogin.models.{LoginFailed, LoginRequest, TestIndividual}
import uk.gov.hmrc.api.testlogin.services.LoginService
import uk.gov.hmrc.domain.{Nino, SaUtr}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future.failed

class LoginServiceSpec extends UnitSpec with MockitoSugar {

  val user = TestIndividual("543212311772", SaUtr("1097172564"), Nino("AA100010B"))

  trait Setup {
    implicit val hc = HeaderCarrier()
    val underTest = new LoginService {
      override val apiPlatformTestUserConnector: ApiPlatformTestUserConnector = mock[ApiPlatformTestUserConnector]
      override val authLoginStubConnector: AuthLoginStubConnector = mock[AuthLoginStubConnector]
    }
  }

  "login" should {
    "return the session when the authentication is successful" in new Setup {

      val loginRequest = LoginRequest("user", "password")

      given(underTest.apiPlatformTestUserConnector.authenticate(loginRequest)).willReturn(user)
      val userSession = "mdtp=session"
      given(underTest.authLoginStubConnector.createSession(user)).willReturn(userSession)

      val result = await(underTest.authenticate(loginRequest))

      result shouldBe userSession
    }

    "propagate LoginFailed exception when authentication fails" in new Setup {

      val loginRequest = LoginRequest("user", "password")

      given(underTest.apiPlatformTestUserConnector.authenticate(loginRequest)).willReturn(failed(new LoginFailed("user")))

      intercept[LoginFailed]{await(underTest.authenticate(loginRequest))}
    }
  }
}
