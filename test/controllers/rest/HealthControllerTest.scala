package controllers.rest

import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import testutils.TestApplicationFactory

import scala.concurrent.Future

class HealthControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  /** ————————————————— */
  /** HEALTH CONTROLLER */
  /** ————————————————— */
  "HealthController" should {
    "accept GET request on base path" in {
      val future: Future[Result] =
        route(app, FakeRequest(GET, "/").withHeaders(("Authorization", "GlobalAdmin"))).get
      status(future) mustEqual 200
    }
  }
}
