package uk.gov.hmrc.testlogin.views

import javax.inject.Inject
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.api.testlogin.views.html.{ErrorView, GovUkWrapper}
import uk.gov.hmrc.api.testlogin.AsyncHmrcSpec

class ErrorViewSpec @Inject()(govUkWrapper: GovUkWrapper) extends AsyncHmrcSpec with GuiceOneServerPerSuite {
  "Error template page" should {
    "render correctly when when title, heading and message" in {
      val message = "Error Message"

      val messages = app.injector.instanceOf[Messages]

      val page = new ErrorView(govUkWrapper).render("", "", message, FakeRequest(), messages)

      page.body should include(message)
    }
  }
}