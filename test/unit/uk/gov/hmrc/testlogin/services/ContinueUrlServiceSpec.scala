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

package unit.uk.gov.hmrc.testlogin.services

import org.mockito.BDDMockito.given
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.api.testlogin.config.AppConfig
import uk.gov.hmrc.api.testlogin.services.ContinueUrlService
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.test.UnitSpec

class ContinueUrlServiceSpec extends UnitSpec with MockitoSugar {

  trait Setup {
    val appConfig = mock[AppConfig]
    val underTest = new ContinueUrlService(appConfig)
  }

  "ContinueUrlService.isValidContinueUrl" should {

    "return true when actual URL matches configured absolute URL" in new Setup {
      given(appConfig.continueUrl).willReturn("http://localhost:9610/oauth/grantscope")
      underTest.isValidContinueUrl("http://localhost:9610/oauth/grantscope?auth_id=59d4de133500009100a027a8") shouldBe true
    }

    "return false when actual URL does not match configured absolute URL" in new Setup {
      given(appConfig.continueUrl).willReturn("http://localhost:9610/oauth/grantscope")
      underTest.isValidContinueUrl("http://nefarious-server.net:9610/oauth/grantscope?auth_id=59d4de133500009100a027a8") shouldBe false
    }

    "return true when actual matches configured relative URL" in new Setup {
      given(appConfig.continueUrl).willReturn("/oauth/grantscope")
      underTest.isValidContinueUrl("/oauth/grantscope?auth_id=59d4deb12600002700e141b8") shouldBe true
    }

    "return false when actual does not match configured relative URL" in new Setup {
      given(appConfig.continueUrl).willReturn("/oauth/grantscope")
      underTest.isValidContinueUrl("http://nefarious-server.net/oauth/grantscope?auth_id=59d4deb12600002700e141b8") shouldBe false
    }
  }
}
