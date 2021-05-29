package tests.rest

import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import testutils.Defaults.{
  descriptionOfRESTTodo,
  descriptionOfRESTTodoUpdated,
  millis,
  titleOfRESTTodo,
  titleOfRESTTodoUpdated,
  todoRoute
}
import testutils.TestApplicationFactory
import todorestgraphqlsample.models.api.Todo

import scala.concurrent.Future

class TodoIntegrationTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  "A Todo (REST)" must {
    "be creatable" in {
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
  }

  "be gettable" in {
    val futureResult: Future[Result] = route(
      app,
      FakeRequest(GET, todoRoute + s"/2")
    ).get
    // Check Http Status
    status(futureResult) mustBe 200

    // Check content type
    contentType(futureResult) mustBe Some("application/json")

    val content = contentAsJson(futureResult)
    // Check returned content has format of model Todo
    content.validate[Todo].isSuccess mustBe true

    // Check returned content contains todo
    content mustEqual Json.obj(
      "id"          -> 2,
      "title"       -> s"$titleOfRESTTodo",
      "description" -> s"$descriptionOfRESTTodo",
      "isDone"      -> false,
      "createdAt"   -> millis
    )
  }

  "be updatable" in {
    val futureResult: Future[Result] = route(
      app,
      FakeRequest(PATCH, todoRoute).withJsonBody(
        Json.parse(
          s"""{
             |  "id": 2,
             |  "title": "${titleOfRESTTodoUpdated}",
             |  "description": "${descriptionOfRESTTodoUpdated}",
             |  "isDone": true,
             |  "createdAt": $millis
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

    // Check returned content contains updated todo
    content mustEqual Json.obj(
      "id"          -> 2,
      "title"       -> s"$titleOfRESTTodoUpdated",
      "description" -> s"$descriptionOfRESTTodoUpdated",
      "isDone"      -> true,
      "doneAt"      -> millis,
      "createdAt"   -> millis
    )
  }

  "occur when getting all" in {
    val futureResult: Future[Result] = route(
      app,
      FakeRequest(GET, todoRoute)
    ).get
    // Check Http Status
    status(futureResult) mustBe 200

    // Check content type
    contentType(futureResult) mustBe Some("application/json")

    val content = contentAsJson(futureResult)
    // Check returned content has format of model Todo
    content.validate[Seq[Todo]].isSuccess mustBe true

    // Check returned content contains updated todo
    content mustEqual Json.arr(
      Json.obj(
        "id"          -> 1,
        "title"       -> "Testtodo 1",
        "description" -> "Dies ist ein Test",
        "isDone"      -> false,
        "createdAt"   -> millis
      ),
      Json.obj(
        "id"          -> 2,
        "title"       -> s"$titleOfRESTTodoUpdated",
        "description" -> s"$descriptionOfRESTTodoUpdated",
        "isDone"      -> true,
        "doneAt"      -> millis,
        "createdAt"   -> millis
      )
    )
  }

  "be deletable" in {
    val futureResult: Future[Result] = route(
      app,
      FakeRequest(DELETE, todoRoute + s"/2")
    ).get
    // Check Http Status
    status(futureResult) mustBe 200

    // Check content type
    contentType(futureResult) mustBe Some("application/json")

    val content = contentAsJson(futureResult)
    // Check returned content has format of model Todo
    content.validate[Todo].isSuccess mustBe true

    // Check returned content contains updated todo
    content mustEqual Json.obj(
      "id"          -> 2,
      "title"       -> s"$titleOfRESTTodoUpdated",
      "description" -> s"$descriptionOfRESTTodoUpdated",
      "isDone"      -> true,
      "doneAt"      -> millis,
      "createdAt"   -> millis
    )
  }
}
