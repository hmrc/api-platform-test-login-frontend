package uk.gov.hmrc.testlogin.views

import javax.inject.Inject
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.api.testlogin.views.html.{ErrorView, GovUkWrapper}
import uk.gov.hmrc.play.test.UnitSpec

class ErrorViewSpec @Inject()(govUkWrapper: GovUkWrapper) extends UnitSpec with MockitoSugar with GuiceOneServerPerSuite {
  "Error template page" should {
    "render correctly when given title, heading and message" in {
      val message = "Error Message"

      val messages = app.injector.instanceOf[Messages]

      val page = new ErrorView(govUkWrapper).render("", "", message, FakeRequest(), messages)

      page.body should include(message)
    }
  }
}