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

import uk.gov.hmrc.api.testlogin.helpers.WebPage

class ContinuePage(port: Int) extends WebPage {
  import ContinuePage._
  override val url: String = s"http://localhost:$port$path"

  val pageTitle = "Continue Page"
}

object ContinuePage {
  val path = "/continue"
}
