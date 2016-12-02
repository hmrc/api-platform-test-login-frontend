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

package it.uk.gov.hmrc.api.testlogin.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import it.uk.gov.hmrc.api.testlogin.MockHost
import org.apache.http.HttpStatus._
import uk.gov.hmrc.crypto.{PlainText, ApplicationCrypto}

object AuthLoginStub extends MockHost(11112) {

  def givenNextAuthSessionReturnedForUserIs(username: String, authSession: String) = {
    val encryptedSession = ApplicationCrypto.SessionCookieCrypto.encrypt(PlainText(authSession)).value
    mock.register(post(urlPathEqualTo("/auth-login-stub/gg-sign-in"))
      .withRequestBody(containing(s"authorityId=$username"))
      .willReturn(aResponse()
        .withStatus(SC_SEE_OTHER)
        .withHeader("Set-Cookie", s"mdtp=$encryptedSession")))
  }
}
