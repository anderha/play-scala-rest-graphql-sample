package tests.graphql

import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json.{ JsError, JsSuccess, JsValue, Json }
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{ route, POST }
import testutils.Defaults.{
  descriptionOfGraphQLTodo,
  descriptionOfGraphQLTodoUpdated,
  graphQlEndpoint,
  millis,
  titleOfGraphQLTodo,
  titleOfGraphQLTodoUpdated,
  todoGQLSchema
}
import testutils.TestApplicationFactory
import play.api.test.Helpers._

import scala.concurrent.Future

class TodoIntegrationTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  "A Todo (GraphQL)" must {
    "be creatable with createTodo" in {
      val futureResult: Future[Result] = route(
        app,
        FakeRequest(POST, "/graphql").withJsonBody(
          Json.obj(
            "operationName" -> "CreateTodo",
            "query"         -> s""" 
                          |mutation CreateTodo(${"$todoToCreate"}: TodoToCreateType!) {
                          | createTodo(todoToCreate: ${"$todoToCreate"}) {
                          |   ${todoGQLSchema}
                          | }
                          |}""".stripMargin,
            "variables"     -> Json.obj(
              "todoToCreate" -> Json.obj(
                "title"       -> s"${titleOfGraphQLTodo}",
                "description" -> s"${descriptionOfGraphQLTodo}"
              )
            )
          )
        )
      ).get

      // Check Http Status
      status(futureResult) mustBe 200

      // Check content type
      contentType(futureResult) mustBe Some("application/json")

      val content = contentAsJson(futureResult)
      // Check returned content contains created todo
      content mustEqual Json.obj(
        "data" -> Json.obj(
          "createTodo" -> Json.obj(
            "id"          -> 2,
            "title"       -> s"$titleOfGraphQLTodo",
            "description" -> s"$descriptionOfGraphQLTodo",
            "isDone"      -> false,
            "doneAt"      -> null,
            "createdAt"   -> millis
          )
        )
      )
    }
  }

  "be gettable with todo(id)" in {
    val futureResult: Future[Result] = route(
      app,
      FakeRequest(POST, "/graphql").withJsonBody(
        Json.obj(
          "operationName" -> "GetTodo",
          "query"         -> s""" 
                        |query GetTodo(${"$id"}: Long!) {
                        | todo(id: ${"$id"}) {
                        |   ${todoGQLSchema}
                        | }
                        |}""".stripMargin,
          "variables"     -> Json.obj(
            "id" -> 2
          )
        )
      )
    ).get

    // Check Http Status
    status(futureResult) mustBe 200

    // Check content type
    contentType(futureResult) mustBe Some("application/json")

    val content = contentAsJson(futureResult)
    // Check returned content contains todo
    content mustEqual Json.obj(
      "data" -> Json.obj(
        "todo" -> Json.obj(
          "id"          -> 2,
          "title"       -> s"$titleOfGraphQLTodo",
          "description" -> s"$descriptionOfGraphQLTodo",
          "isDone"      -> false,
          "doneAt"      -> null,
          "createdAt"   -> millis
        )
      )
    )
  }

  "be patchable with updateTodo" in {
    val futureResult: Future[Result] = route(
      app,
      FakeRequest(POST, "/graphql").withJsonBody(
        Json.obj(
          "operationName" -> "UpdateTodo",
          "query"         -> s""" 
                        |mutation UpdateTodo(${"$todoToUpdate"}: TodoToUpdateType!) {
                        | updateTodo(todoToUpdate: ${"$todoToUpdate"}) {
                        |   ${todoGQLSchema}
                        | }
                        |}""".stripMargin,
          "variables"     -> Json.obj(
            "todoToUpdate" -> Json.obj(
              "id"          -> 2,
              "title"       -> s"$titleOfGraphQLTodoUpdated",
              "description" -> s"$descriptionOfGraphQLTodoUpdated",
              "isDone"      -> true,
              "doneAt"      -> millis,
              "createdAt"   -> millis
            )
          )
        )
      )
    ).get

    // Check Http Status
    status(futureResult) mustBe 200

    // Check content type
    contentType(futureResult) mustBe Some("application/json")

    val content = contentAsJson(futureResult)
    // Check returned content contains updated todo
    content mustEqual Json.obj(
      "data" -> Json.obj(
        "updateTodo" -> Json.obj(
          "id"          -> 2,
          "title"       -> s"$titleOfGraphQLTodoUpdated",
          "description" -> s"$descriptionOfGraphQLTodoUpdated",
          "isDone"      -> true,
          "doneAt"      -> millis,
          "createdAt"   -> millis
        )
      )
    )
  }

  "occur when getting all" in {
    val futureResult: Future[Result] = route(
      app,
      FakeRequest(POST, "/graphql").withJsonBody(
        Json.obj(
          "operationName" -> "GetAll",
          "query"         -> s""" 
                        |query GetAll {
                        | allTodos {
                        |   ${todoGQLSchema}
                        | }
                        |}""".stripMargin
        )
      )
    ).get

    // Check Http Status
    status(futureResult) mustBe 200

    // Check content type
    contentType(futureResult) mustBe Some("application/json")

    val content = contentAsJson(futureResult)
    // Check returned content contains created todo
    content mustEqual Json.obj(
      "data" -> Json.obj(
        "allTodos" -> Json.arr(
          Json.obj(
            "id"          -> 1,
            "title"       -> "Testtodo 1",
            "description" -> "Dies ist ein Test",
            "isDone"      -> false,
            "doneAt"      -> null,
            "createdAt"   -> millis
          ),
          Json.obj(
            "id"          -> 2,
            "title"       -> s"$titleOfGraphQLTodoUpdated",
            "description" -> s"$descriptionOfGraphQLTodoUpdated",
            "isDone"      -> true,
            "doneAt"      -> millis,
            "createdAt"   -> millis
          )
        )
      )
    )
  }

  "be deletable by deleteTodo" in {
    val futureResult: Future[Result] = route(
      app,
      FakeRequest(POST, "/graphql").withJsonBody(
        Json.obj(
          "operationName" -> "Delete",
          "query"         -> s""" 
                        |mutation Delete(${"$id"}: Long!) {
                        | deleteTodo(id: ${"$id"}) {
                        |   ${todoGQLSchema}
                        | }
                        |}""".stripMargin,
          "variables"     -> Json.obj(
            "id" -> 2
          )
        )
      )
    ).get

    // Check Http Status
    status(futureResult) mustBe 200

    // Check content type
    contentType(futureResult) mustBe Some("application/json")

    val content = contentAsJson(futureResult)
    // Check returned content contains created todo
    content mustEqual Json.obj(
      "data" -> Json.obj(
        "deleteTodo" -> Json.obj(
          "id"          -> 2,
          "title"       -> s"$titleOfGraphQLTodoUpdated",
          "description" -> s"$descriptionOfGraphQLTodoUpdated",
          "isDone"      -> true,
          "doneAt"      -> millis,
          "createdAt"   -> millis
        )
      )
    )
  }
}
