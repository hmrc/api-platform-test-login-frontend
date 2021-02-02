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

package uk.gov.hmrc.testlogin.services


import _root_.uk.gov.hmrc.http.SessionKeys.{sessionId, token}
import org.joda.time.DateTimeUtils.{setCurrentMillisFixed, setCurrentMillisSystem}
import org.scalatest.BeforeAndAfterAll
import play.api.mvc.Session
import uk.gov.hmrc.api.testlogin.connectors.ApiPlatformTestUserConnector
import uk.gov.hmrc.api.testlogin.models._
import uk.gov.hmrc.api.testlogin.services.LoginService
import uk.gov.hmrc.domain.{Nino, SaUtr}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.api.testlogin.AsyncHmrcSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.{successful, failed}

class LoginServiceSpec extends AsyncHmrcSpec with BeforeAndAfterAll {

  val user = TestIndividual("543212311772", SaUtr("1097172564"), Nino("AA100010B"))

  trait Setup {
    implicit val hc = HeaderCarrier()
    val apiPlatformTestUserConnector = mock[ApiPlatformTestUserConnector]
    val underTest = new LoginService(apiPlatformTestUserConnector)
  }

  override def beforeAll() {
    setCurrentMillisFixed(10000)
  }

  override def afterAll() {
    setCurrentMillisSystem()
  }

  "login" should {
    "return the session when the authentication is successful" in new Setup {

      val loginRequest = LoginRequest("user", "password")
      val authSession = AuthenticatedSession("Bearer AUTH_TOKEN", "/auth/oid/12345", "GG_TOKEN", "Individual")

      when(apiPlatformTestUserConnector.authenticate(loginRequest)).thenReturn(successful(authSession))

      val result = await(underTest.authenticate(loginRequest))

      result shouldBe Session(Map(
        SessionKeys.affinityGroup -> "Individual",
        SessionKeys.name -> loginRequest.username,
        SessionKeys.authToken -> authSession.authBearerToken,
        SessionKeys.lastRequestTimestamp -> "10000",
        SessionKeys.authProvider -> "GGW",
        token -> authSession.gatewayToken,
        sessionId -> result.data(sessionId),
        SessionKeys.userId -> authSession.authorityURI)
      )
    }

    "propagate LoginFailed exception when authentication fails" in new Setup {

      val loginRequest = LoginRequest("user", "password")

      when(apiPlatformTestUserConnector.authenticate(loginRequest)).thenReturn(failed(new LoginFailed("user")))

      intercept[LoginFailed] {
        await(underTest.authenticate(loginRequest))
      }
    }
  }
}
