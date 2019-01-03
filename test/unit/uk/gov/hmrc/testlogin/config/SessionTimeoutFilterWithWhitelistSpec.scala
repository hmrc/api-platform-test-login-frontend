/*
 * Copyright 2019 HM Revenue & Customs
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

package unit.uk.gov.hmrc.testlogin.config

import org.joda.time.{DateTime, DateTimeZone, Duration}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import play.api.mvc._
import play.api.test.FakeRequest
import uk.gov.hmrc.api.testlogin.config.{SessionTimeoutFilterWithWhitelist, WhitelistedCall}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class SessionTimeoutFilterWithWhitelistSpec extends UnitSpec with MockitoSugar with ScalaFutures with WithFakeApplication {

  trait Setup {
    val filter = new SessionTimeoutFilterWithWhitelist(
      timeoutDuration = Duration.standardSeconds(1),
      whitelistedCalls = Set(WhitelistedCall("/login", "GET")),
      onlyWipeAuthToken = false
    )

    val nextOperationFunction = mock[RequestHeader => Future[Result]]

    when(nextOperationFunction.apply(any())).thenAnswer(new Answer[Future[Result]] {
      override def answer(invocation: InvocationOnMock): Future[Result] = {
        val headers = invocation.getArguments.head.asInstanceOf[RequestHeader]
        Future.successful(Results.Ok.withSession(headers.session + ("authToken" -> "Bearer Token")))
      }
    })

    def twoSecondsAgo: String = {
      DateTime.now(DateTimeZone.UTC).minusSeconds(2).getMillis.toString
    }
  }

  "apply" should {

    "leave the session keys intact when path in whitelist" in new Setup {
      val request = FakeRequest(method = "GET", path = "/login")

      whenReady(filter.apply(nextOperationFunction)(request.withSession("key" -> "value"))) { result =>
        result.session(request).data("authToken") shouldBe "Bearer Token"
      }

      verify(nextOperationFunction).apply(any())
    }

    "leave the session keys when path not in whitelist" in new Setup {
      val request = FakeRequest(method = "GET", path = "/dashboard")

      whenReady(filter.apply(nextOperationFunction)(request.withSession("key" -> "value"))) { result =>
        val sessionData = result.session(request).data
        sessionData.size shouldBe 3
        sessionData.isDefinedAt("ts") shouldBe true
        sessionData.isDefinedAt("key") shouldBe true
      }

      verify(nextOperationFunction).apply(any())
    }

    "leave the session keys when path in whitelist with different method" in new Setup {
      val request = FakeRequest(method = "POST", path = "/login")

      whenReady(filter.apply(nextOperationFunction)(request.withSession("key" -> "value"))) { result =>
        val sessionData = result.session(request).data
        sessionData.size shouldBe 3
        sessionData.isDefinedAt("ts") shouldBe true
        sessionData.isDefinedAt("key") shouldBe true
      }

      verify(nextOperationFunction).apply(any())
    }

  }

}
