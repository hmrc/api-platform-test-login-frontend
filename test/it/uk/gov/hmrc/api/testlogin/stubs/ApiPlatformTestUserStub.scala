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

package it.uk.gov.hmrc.api.testlogin.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import it.uk.gov.hmrc.api.testlogin.MockHost
import org.apache.http.HttpStatus._
import play.api.libs.json.Json
import uk.gov.hmrc.api.testlogin.models.JsonFormatters.{formatLoginRequest, formatTestUser}
import uk.gov.hmrc.api.testlogin.models.{LoginRequest, TestIndividual}

object ApiPlatformTestUserStub extends MockHost(11111) {

  def givenIndividualHasPassword(individual: TestIndividual, password: String) = {
    mock.register(post(urlPathEqualTo("/authenticate"))
      .willReturn(aResponse()
        .withStatus(SC_UNAUTHORIZED)
        .withHeader("Content-Type", "application/json")))

    mock.register(post(urlPathEqualTo("/authenticate"))
      .withRequestBody(equalToJson(Json.toJson(LoginRequest(individual.username, password)).toString()))
      .willReturn(aResponse()
        .withStatus(SC_OK)
        .withHeader("Content-Type", "application/json")
        .withBody(Json.toJson(individual).toString())))
  }
}
