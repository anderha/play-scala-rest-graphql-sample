package tests.rest

import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import testutils.Defaults.{ descriptionOfRESTTodo, millis, titleOfRESTTodo, todoRoute }
import testutils.TestApplicationFactory
import todorestgraphqlsample.models.api.Todo

import scala.concurrent.Future

class TodoControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  "POST on route of todo" must {
    "return a newly created todo" in {
      val futureResult: Future[Result] = route(
        app,
        FakeRequest(POST, todoRoute).withJsonBody(
          Json.parse(
            s"""{
               |  "title": "${titleOfRESTTodo}",
               |  "description": "${descriptionOfRESTTodo}" 
               |}""".stripMargin
          )
        )
      ).get
      // Check Http Status
      status(futureResult) mustBe 200

      // Check content type
      contentType(futureResult) mustBe Some("application/json")

      val content = contentAsJson(futureResult)
      // Check returned content has format of model Todo
      content.validate[Todo].isSuccess mustBe true

      // Check returned content contains created todo
      content mustEqual Json.obj(
        "id"          -> 2,
        "title"       -> s"$titleOfRESTTodo",
        "description" -> s"$descriptionOfRESTTodo",
        "isDone"      -> false,
        "createdAt"   -> millis
      )
    }

    "return http status code 400 on malformed body" in {
      val futureResult: Future[Result] = route(
        app,
        FakeRequest(POST, todoRoute).withJsonBody(Json.parse("{}"))
      ).get
      // Check Http Status
      status(futureResult) mustBe 400
    }

    "return http status code 400 on empty title" in {
      val futureResult: Future[Result] = route(
        app,
        FakeRequest(POST, todoRoute).withJsonBody(
          Json.parse(
            s"""{
               |  "title": "",
               |  "description": "$descriptionOfRESTTodo" 
               |}""".stripMargin
          )
        )
      ).get
      // Check Http Status
      status(futureResult) mustBe 400
    }
  }

  "GET on TodoController(REST)" must {
    "return a list of todos" in {
      val futureResult: Future[Result] = route(
        app,
        FakeRequest(GET, todoRoute)
      ).get
      // Check Http Status
      status(futureResult) mustBe 200
      val content                      = contentAsJson(futureResult)

      // Check returned content is a sequence of todos
      content.validate[Seq[Todo]].isSuccess mustBe true
    }
  }
}
