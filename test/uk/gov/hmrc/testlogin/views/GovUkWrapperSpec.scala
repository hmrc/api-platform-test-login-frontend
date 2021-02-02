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

package uk.gov.hmrc.testlogin.views

import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.api.testlogin.config.AppConfig
import play.api.i18n.MessagesProvider
import play.api.i18n.MessagesImpl
import play.api.i18n.DefaultMessagesApi
import play.api.i18n.Lang
import uk.gov.hmrc.api.testlogin.views.html._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.api.testlogin.AsyncHmrcSpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class GovUkWrapperSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(("metrics.jvm", false))
      .build()

  "GovUKWrapper" should {

    trait Setup {
      implicit val fakeRequest = FakeRequest()

      implicit val messagesProvider: MessagesProvider = MessagesImpl(Lang(java.util.Locale.ENGLISH), new DefaultMessagesApi())

      val govUkWrapper = app.injector.instanceOf[GovUkWrapper]
      val appConfig: AppConfig = mock[AppConfig]
      when(appConfig.analyticsHost).thenReturn("")
      when(appConfig.analyticsToken).thenReturn("")
    }

    "Indicate that embedded Microsoft browsers should render using the latest browser version available" in new Setup {

      val mainView: Html = govUkWrapper(Some("Test"))(Html.apply("<h1>Test</h1>"))
      mainView.body should include("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">")

    }
  }
}

