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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.Application
import play.api.i18n.{DefaultMessagesApi, Lang, MessagesImpl, MessagesProvider}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.api.testlogin.AsyncHmrcSpec
import uk.gov.hmrc.api.testlogin.config.AppConfig
import uk.gov.hmrc.api.testlogin.views.html._

class GovUkWrapperSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(("metrics.jvm", false))
      .build()

  trait Setup {
    implicit val fakeRequest                        = FakeRequest()
    implicit val messagesProvider: MessagesProvider = MessagesImpl(Lang(java.util.Locale.ENGLISH), new DefaultMessagesApi())
    implicit val appConfig: AppConfig               = mock[AppConfig]

    val govUkWrapper = app.injector.instanceOf[GovUkWrapper]
    when(appConfig.analyticsHost).thenReturn("")
    when(appConfig.analyticsToken).thenReturn("")

    val mainView: Html = govUkWrapper(Some("Test"))(Html.apply("<h1>Test</h1>"))

    def elementExistsById(doc: Document, id: String): Boolean = doc.select(s"#$id").html().nonEmpty
  }

  "GovUKWrapper" should {

    "Indicate that embedded Microsoft browsers should render using the latest browser version available" in new Setup {
      mainView.body should include("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">")
    }

    "render the page with feedback banner" in new Setup {
      val document = Jsoup.parse(mainView.body)

      elementExistsById(document, "feedback") shouldBe true
      elementExistsById(document, "show-survey") shouldBe true
      document.getElementById("feedback-title").text() shouldBe "Your feedback helps us improve our service"
    }
  }
}
