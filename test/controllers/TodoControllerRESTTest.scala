package controllers

import de.innfactory.bootstrapplay2.models.api.Todo
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import testutils.Defaults.{ millis, todoRoute }
import testutils.TestApplicationFactory

import scala.concurrent.Future

class TodoControllerRESTTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  private val titleOfTodo       = "REST Testtodo"
  private val descriptionOfTodo = "This is a REST test!"

  "POST on TodoController(REST)" should {
    "return a newly created todo" in {
      val futureResult: Future[Result] = route(
        app,
        FakeRequest(POST, todoRoute).withJsonBody(
          Json.parse(
            s"""{
               |  "title": "${titleOfTodo}",
               |  "description": "${descriptionOfTodo}" 
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
        "title"       -> s"$titleOfTodo",
        "description" -> s"$descriptionOfTodo",
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
               |  "description": "$descriptionOfTodo" 
               |}""".stripMargin
          )
        )
      ).get
      // Check Http Status
      status(futureResult) mustBe 400
    }
  }

  "GET on TodoController(REST)" should {
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
