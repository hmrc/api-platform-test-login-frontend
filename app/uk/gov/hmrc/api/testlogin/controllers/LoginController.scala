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

package uk.gov.hmrc.api.testlogin.controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc.Action
import uk.gov.hmrc.api.testlogin.models.{LoginFailed, LoginRequest}
import uk.gov.hmrc.api.testlogin.services.{LoginServiceImpl, LoginService}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.mvc.Results._

import scala.concurrent.Future
import scala.concurrent.Future.successful

trait LoginController extends FrontendController with I18nSupport {

  val loginService: LoginService

  case class LoginForm(userId: String, password: String, continue: String)

  private val loginForm = Form(
    mapping(
      "userId" -> nonEmptyText,
      "password" -> nonEmptyText,
      "continue" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  )

  def showLoginPage(continue: String) = Action.async { implicit request =>
    successful(Ok(uk.gov.hmrc.api.testlogin.views.html.login(continue)))
  }

  def login() = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => successful(BadRequest(uk.gov.hmrc.api.testlogin.views.html.error_template("", "", "Invalid Parameters"))),
      loginForm => {
        loginService.authenticate(LoginRequest(loginForm.userId, loginForm.password)) map { _ =>
          Redirect(loginForm.continue)
        } recover {
          case e : LoginFailed =>
            Unauthorized(uk.gov.hmrc.api.testlogin.views.html.login(loginForm.continue, Some("Invalid user ID or password. Try again.")))
        }
      }
    )
  }
}

class LoginControllerImpl @Inject()(override val messagesApi: MessagesApi, override val loginService: LoginServiceImpl) extends LoginController
