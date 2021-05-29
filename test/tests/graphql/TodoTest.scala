package tests.graphql

import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import testutils.Defaults.{ graphQlEndpoint, millis }
import testutils.TestApplicationFactory

import scala.concurrent.Future

class TodoTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  private val titleOfTodo       = "GraphQL Testtodo"
  private val descriptionOfTodo = "This is a GraphQL test!"

  private def baseFakeRequest(jsonBody: JsValue): Option[Future[Result]] =
    route(app, FakeRequest(POST, graphQlEndpoint).withJsonBody(jsonBody))

  private val todoGQLSchema = s"""
                                 | id
                                 | title
                                 | description
                                 | isDone
                                 | doneAt
                                 | createdAt
                                 |""".stripMargin

  "Mutation createTodo" must {
    "return a newly created todo" in {
      val futureResult: Future[Result] = route(
        app,
        FakeRequest(POST, "/graphql").withJsonBody(
          Json.obj(
            "operationName" -> "CreateTodo",
            "query"         -> s""" 
                          |mutation CreateTodo {
                          | createTodo(todoToCreate: { 
                          |   title: "${titleOfTodo}", 
                          |   description: "${descriptionOfTodo}"
                          |  }) {
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
          "createTodo" -> Json.obj(
            "id"          -> 2,
            "title"       -> s"$titleOfTodo",
            "description" -> s"$descriptionOfTodo",
            "isDone"      -> false,
            "doneAt"      -> null,
            "createdAt"   -> millis
          )
        )
      )
    }

    "return http status code 400 on malformed body" in {
      val futureResult: Future[Result] = baseFakeRequest(
        Json.obj(
          "operationName" -> "CreateTodo",
          "query"         -> s""" 
                        |mutation CreateTodo {
                        | createTodo {
                        |   ${todoGQLSchema}
                        | }
                        |}""".stripMargin
        )
      ).get

      // Check Http Status
      status(futureResult) mustBe 400
    }

    "return error message 400 on empty title" in {
      val futureResult: Future[Result] = baseFakeRequest(
        Json.obj(
          "operationName" -> "CreateTodo",
          "query"         -> s""" 
                        |mutation CreateTodo {
                        | createTodo(todoToCreate: { title: "", description: "$descriptionOfTodo"}) {
                        |   ${todoGQLSchema}
                        | }
                        |}""".stripMargin
        )
      ).get

      // Check Http Status
      status(futureResult) mustBe 200

      // Check content type
      contentType(futureResult) mustBe Some("application/json")

      val content = contentAsJson(futureResult)
      // Check returned content contains error
      content mustEqual Json.obj(
        "data"   -> null,
        "errors" -> Json.arr(
          Json.obj(
            "message"    -> "Bad Request",
            "path"       -> Json.arr(
              "createTodo"
            ),
            "locations"  -> Json.arr(
              Json.obj("line" -> 3, "column" -> 2)
            ),
            "extensions" -> Json.obj("ERROR_CODE" -> 400, "ERROR_MESSAGE" -> "Bad Request")
          )
        )
      )

    }
  }

  "Query allTodos" must {
    "return a list of todos" in {
      val futureResult: Future[Result] = baseFakeRequest(
        Json.obj(
          "operationName" -> "AllTodos",
          "query"         -> s"""query AllTodos {
                        | allTodos {
                        |   ${todoGQLSchema}
                        | }
                        |}""".stripMargin
        )
      ).get
      // Check Http Status
      status(futureResult) mustBe 200
      // Check content type
      contentType(futureResult) mustBe Some("application/json")

      val content = contentAsJson(futureResult)
      // Check returned content has format of todo
      content mustEqual Json.obj(
        "data" -> Json.obj(
          "allTodos" -> Json.arr(
            Json.parse(
              s"""
                 |{
                 |   "id": 1,
                 |   "title": "Testtodo 1",
                 |   "description": "Dies ist ein Test",
                 |   "isDone": false,
                 |   "doneAt": null,
                 |   "createdAt": $millis
                 | }
                 |""".stripMargin
            ),
            Json.parse(
              s"""
                 |{
                 |   "id": 2,
                 |   "title": "${titleOfTodo}",
                 |   "description": "${descriptionOfTodo}",
                 |   "isDone": false,
                 |   "doneAt": null,
                 |   "createdAt": $millis
                 | }
                 |""".stripMargin
            )
          )
        )
      )
    }
  }
}
