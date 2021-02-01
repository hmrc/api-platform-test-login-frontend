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

package uk.gov.hmrc.testlogin.config

import akka.stream.Materializer
import org.joda.time.{DateTime, DateTimeZone, Duration}
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc._
import play.api.test.FakeRequest
import uk.gov.hmrc.api.testlogin.config.SessionTimeoutFilterWithWhitelist
import uk.gov.hmrc.play.bootstrap.filters.frontend.SessionTimeoutFilterConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.testlogin.AsyncHmrcSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class SessionTimeoutFilterWithWhitelistSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite {

  trait Setup {

    implicit val mat = app.injector.instanceOf[Materializer]

    val config = new SessionTimeoutFilterConfig(
      timeoutDuration = Duration.standardSeconds(1),
      onlyWipeAuthToken = false
    )

    val filter = new SessionTimeoutFilterWithWhitelist(config)

    val nextOperationFunction = mock[RequestHeader => Future[Result]]

    when(nextOperationFunction.apply(*)).thenAnswer( (invocation: InvocationOnMock) => {
      val headers = invocation.getArguments.head.asInstanceOf[RequestHeader]
      Future.successful(Results.Ok.withSession(headers.session + ("authToken" -> "Bearer Token")))
    })

    def twoSecondsAgo: String = {
      DateTime.now(DateTimeZone.UTC).minusSeconds(2).getMillis.toString
    }
  }

  "apply" should {

    "leave the session keys intact when path in whitelist" in new Setup {
      val request = FakeRequest(method = "GET", path = "/login")

      val result = await(filter.apply(nextOperationFunction)(request.withSession("key" -> "value")))
      result.session(request).data("authToken") shouldBe "Bearer Token"

      verify(nextOperationFunction).apply(*)
    }

    "leave the session keys when path not in whitelist" in new Setup {
      val request = FakeRequest(method = "GET", path = "/dashboard")

      val result = await(filter.apply(nextOperationFunction)(request.withSession("key" -> "value")))
      val sessionData = result.session(request).data
      sessionData.size shouldBe 3
      sessionData.isDefinedAt("ts") shouldBe true
      sessionData.isDefinedAt("key") shouldBe true

      verify(nextOperationFunction).apply(*)
    }

    "leave the session keys when path in whitelist with different method" in new Setup {
      val request = FakeRequest(method = "POST", path = "/login")

      val result = await(filter.apply(nextOperationFunction)(request.withSession("key" -> "value")))
      val sessionData = result.session(request).data
      sessionData.size shouldBe 3
      sessionData.isDefinedAt("ts") shouldBe true
      sessionData.isDefinedAt("key") shouldBe true

      verify(nextOperationFunction).apply(*)
    }

  }

}
