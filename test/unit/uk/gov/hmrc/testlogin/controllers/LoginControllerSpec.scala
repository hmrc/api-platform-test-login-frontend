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

package unit.uk.gov.hmrc.testlogin.controllers


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.mockito.Matchers.{any, refEq}
import org.mockito.BDDMockito.given
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerTest
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContent, Action}
import play.api.test.FakeRequest
import uk.gov.hmrc.api.testlogin.controllers.LoginController
import uk.gov.hmrc.api.testlogin.models.{TestIndividual, LoginFailed, LoginRequest}
import uk.gov.hmrc.api.testlogin.services.LoginService
import uk.gov.hmrc.domain.{Nino, SaUtr}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future.failed

class LoginControllerSpec extends UnitSpec with MockitoSugar with OneAppPerTest {


  trait Setup {
    implicit val materializer = ActorMaterializer.create(ActorSystem.create())
    private val csrfAddToken = app.injector.instanceOf[play.filters.csrf.CSRFAddToken]

    val underTest = new LoginController {
      override val loginService: LoginService = mock[LoginService]

      override def messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
    }

    def execute[T <: play.api.mvc.AnyContent](action: Action[AnyContent], request: FakeRequest[T] = FakeRequest()) = await(csrfAddToken(action)(request))
  }

  "showLoginPage" should {
    "display the login page" in new Setup {

      val result = execute(underTest.showLoginPage("/continueUrl"))

      bodyOf(result) should include ("Sign in")
    }
  }

  "login" should {

    val loginRequest = LoginRequest("aUser", "aPassword")
    val continueUrl = "/continueUrl"
    val request = FakeRequest()
      .withFormUrlEncodedBody("userId" -> loginRequest.username, "password" -> loginRequest.password, "continue" -> continueUrl)

    "display invalid userId or password when the credentials are invalid" in new Setup {

      given(underTest.loginService.authenticate(refEq(loginRequest))(any[HeaderCarrier]())).willReturn(failed(new LoginFailed("")))

      val result = execute(underTest.login(), request = request)

      bodyOf(result) should include ("Invalid user ID or password. Try again.")
    }

    "redirect to the continueUrl when the credentials are valid" in new Setup {
      val testUser = TestIndividual("543212311772", SaUtr("1097172564"), Nino("AA100010B"))

      given(underTest.loginService.authenticate(refEq(loginRequest))(any[HeaderCarrier]())).willReturn(testUser)

      val result = execute(underTest.login(), request = request)

      status(result) shouldBe 303
      result.header.headers("Location") shouldEqual continueUrl
    }

  }
}
