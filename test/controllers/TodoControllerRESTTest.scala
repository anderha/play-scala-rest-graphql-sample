package controllers

import de.innfactory.bootstrapplay2.models.api.Todo
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import testutils.TestApplicationFactory

import scala.concurrent.Future

class TodoControllerRESTTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  private val todoRoute = "/v1/todo"

  "POST on TodoController(REST)" should {
    "return a newly created todo" in {
      val titleOfTodo            = "REST Testtodo"
      val descriptionOfTodo      = "This is a REST test!"
      val future: Future[Result] = route(
        app,
        FakeRequest(POST, "/v1/todo").withJsonBody(
          Json.parse(
            s"""{
               |  "title": "${titleOfTodo}",
               |  "description": "${descriptionOfTodo}" 
               |}""".stripMargin
          )
        )
      ).get
      // Check Http Status
      status(future) mustBe 200
      // Check content type
      contentType(future) mustBe Some("application/json")

      val content = contentAsJson(future)
      // Check returned content has format of todo
      content.validate[Todo].isSuccess mustBe true

      val parsed = content.as[Todo]
      // Check values of created todo
      parsed.title mustBe titleOfTodo
      parsed.description mustBe descriptionOfTodo
      parsed.isDone mustBe false
      parsed.doneAt mustBe None
    }

    "return http status code 400 on malformed body" in {
      val future: Future[Result] = route(
        app,
        FakeRequest(POST, todoRoute).withJsonBody(Json.parse("{}"))
      ).get
      // Check Http Status
      status(future) mustBe 400
    }

    "return http status code 400 on empty title" in {
      val future: Future[Result] = route(
        app,
        FakeRequest(POST, todoRoute).withJsonBody(
          Json.parse(
            s"""{
               |  "title": "",
               |  "description": "" 
               |}""".stripMargin
          )
        )
      ).get
      // Check Http Status
      status(future) mustBe 400
    }
  }

  "GET on TodoController(REST)" should {
    "return a list of todos" in {
      val future: Future[Result] = route(
        app,
        FakeRequest(GET, todoRoute)
      ).get
      // Check Http Status
      status(future) mustBe 200
      val content                = contentAsJson(future)

      // Check returned content is a sequence of todos
      content.validate[Seq[Todo]].isSuccess mustBe true
    }
  }
}
