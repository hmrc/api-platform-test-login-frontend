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

package uk.gov.hmrc.api.testlogin.controllers

import javax.inject.{Inject, Singleton}

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, Request}
import uk.gov.hmrc.api.testlogin.config.AppConfig
import uk.gov.hmrc.api.testlogin.models.{LoginFailed, LoginRequest}
import uk.gov.hmrc.api.testlogin.services.{ContinueUrlService, LoginService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.api.testlogin.views.html.{login => login_template}

import scala.concurrent.Future.successful
import akka.stream.Materializer
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import scala.concurrent.ExecutionContext
import play.api.mvc.MessagesControllerComponents

@Singleton
class LoginController @Inject()(
    loginService: LoginService,
    errorHandler: ErrorHandler,
    continueUrlService: ContinueUrlService,
    mcc: MessagesControllerComponents
)(
    implicit val mat: Materializer,
    val appConfig: AppConfig,
    val ec: ExecutionContext
) extends FrontendController(mcc) {

  case class LoginForm(userId: String, password: String, continue: String)

  private val loginForm = Form(
    mapping(
      "userId" -> nonEmptyText,
      "password" -> nonEmptyText,
      "continue" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  )

  def showLoginPage(continue: String) = Action.async { implicit request =>
    if(continueUrlService.isValidContinueUrl(continue)) successful(Ok(login_template(continue))) else badRequest()
  }

  def login() = Action.async { implicit request =>

    def handleLogin(loginForm: LoginForm) = {
      loginService.authenticate(LoginRequest(loginForm.userId, loginForm.password)) map { session =>
        Redirect(loginForm.continue).withSession(session)
      } recover {
        case e : LoginFailed =>
          Unauthorized(login_template(loginForm.continue, Some("Invalid user ID or password. Try again.")))
      }
    }

    loginForm.bindFromRequest.fold(
      formWithErrors => badRequest(),
      loginForm => if(continueUrlService.isValidContinueUrl(loginForm.continue)) handleLogin(loginForm) else badRequest()
    )
  }

  private def badRequest()(implicit request: Request[AnyContent]) = successful(BadRequest(errorHandler.standardErrorTemplate("", "", "Invalid Parameters")))
}
