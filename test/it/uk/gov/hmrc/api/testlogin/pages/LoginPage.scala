/*
 * Copyright 2017 HM Revenue & Customs
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

package it.uk.gov.hmrc.api.testlogin.pages

import it.uk.gov.hmrc.api.testlogin.helpers.WebPage

object LoginPage extends WebPage {

  override val url: String = s"http://localhost:9000/api-test-login/sign-in?continue=${ContinuePage.url}"

  override def isCurrentPage: Boolean = find(cssSelector("h1")).fold(false)(_.text == "Sign in")
}
