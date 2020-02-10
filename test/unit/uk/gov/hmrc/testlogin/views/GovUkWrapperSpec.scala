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

package unit.uk.gov.hmrc.testlogin.views

import org.mockito.Mockito.when
import org.scalatest.Matchers
import org.scalatest.mockito.MockitoSugar
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.api.testlogin.config.AppConfig
import uk.gov.hmrc.api.testlogin.views.html
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class GovUkWrapperSpec extends UnitSpec with Matchers with MockitoSugar with WithFakeApplication {

  "GovUKWrapper" should {

    trait Setup {
      implicit val fakeRequest = FakeRequest()
      implicit val application = fakeApplication

      val appConfig: AppConfig = mock[AppConfig]
      when(appConfig.analyticsHost).thenReturn("")
      when(appConfig.analyticsToken).thenReturn("")
    }

    "Indicate that embedded Microsoft browsers should render using the latest browser version available" in new Setup {

      val mainView: Html = html.govuk_wrapper(appConfig, "Test")(fakeRequest, applicationMessages)
      mainView.body should include("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">")

    }
  }
}

