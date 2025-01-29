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

package uk.gov.hmrc.api.testlogin.pages

import org.openqa.selenium.By

import uk.gov.hmrc.api.testlogin.helpers.WebPage

class LoginPage(port: Int, continuePage: ContinuePage) extends WebPage {
  override val url: String = s"http://localhost:$port/api-test-login/sign-in?continue=${continuePage.url}"

  val pageTitle = "Sign in"

  def loginWith(userId: String, password: String): Unit = {
    findElement(By.id("userId")).sendKeys(userId)
    findElement(By.id("password")).sendKeys(password)
    clickOnSubmit()
  }

  def clickOnSubmit(): Unit = {
    findElements(By.id("submit")).head.click()
  }

  def errorText() = {
    findElement(By.className("govuk-error-message")).getText
  }

}
