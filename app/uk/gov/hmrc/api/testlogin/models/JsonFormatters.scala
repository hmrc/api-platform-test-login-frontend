/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.api.testlogin.models

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.play.json.Union

object JsonFormatters {
  implicit val formatTestIndividual = Json.format[TestIndividual]
  implicit val formatTestOrganisation = Json.format[TestOrganisation]

  implicit val formatTestUser: Format[TestUser] = Union.from[TestUser]("userType")
    .and[TestIndividual](UserType.INDIVIDUAL.toString)
    .and[TestOrganisation](UserType.ORGANISATION.toString)
    .format

  implicit val formatLoginRequest = Json.format[LoginRequest]
  implicit val formatAuthenticationResponse = Json.format[AuthenticationResponse]
}
