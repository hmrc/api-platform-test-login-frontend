/*
 * Copyright 2023 HM Revenue & Customs
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

import javax.inject.Inject

import org.scalatestplus.play.guice.GuiceOneServerPerSuite

import play.api.i18n.Messages
import play.api.test.FakeRequest

import uk.gov.hmrc.api.testlogin.AsyncHmrcSpec
import uk.gov.hmrc.api.testlogin.config.AppConfig
import uk.gov.hmrc.api.testlogin.views.html.{ErrorView, GovUkWrapper}

class ErrorViewSpec @Inject() (govUkWrapper: GovUkWrapper) extends AsyncHmrcSpec with GuiceOneServerPerSuite {
  "Error template page" should {
    "render correctly when when title, heading and message" in {
      val message = "Error Message"

      val messages           = app.injector.instanceOf[Messages]
      implicit val appConfig = mock[AppConfig]

      val page = new ErrorView(govUkWrapper).render("", "", message, FakeRequest(), messages, appConfig)

      page.body should include(message)
    }
  }
}
