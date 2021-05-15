package controllers

import de.innfactory.bootstrapplay2.models.api.Todo
import models.graphql.{ GraphQLError, GraphQLErrorResponse }
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json.{ JsObject, JsValue, Json }
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class TodoControllerGraphQLTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  val graphQlEndpoint = "/graphql"
  val millis          = 1621082427626L

  val titleOfTodo       = "GraphQL Testtodo"
  val descriptionOfTodo = "This is a GraphQL test!"

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

  "Mutation createTodo" should {
    "return a newly created todo" in {
      val future: Future[Result] = baseFakeRequest(
        Json.obj(
          "operationName" -> "CreateTodo",
          "query"         -> s""" 
                        |mutation CreateTodo {
                        | createTodo(todoToCreate: { title: "${titleOfTodo}", description: "${descriptionOfTodo}"}) {
                        |   ${todoGQLSchema}
                        | }
                        |}""".stripMargin
        )
      ).get

      // Check Http Status
      status(future) mustBe 200
      // Check content type
      contentType(future) mustBe Some("application/json")

      val content = contentAsJson(future)
      // Check returned content is of format todo
      content mustEqual Json.obj(
        "data" -> Json.obj(
          "createTodo" -> Json.parse(s"""
                                        |{
                                        |   "id": 1,
                                        |   "title": "${titleOfTodo}",
                                        |   "description": "${descriptionOfTodo}",
                                        |   "isDone": false,
                                        |   "doneAt": null,
                                        |   "createdAt": 1621082427626
                                        | }
                                        |""".stripMargin)
        )
      )
    }

    "return http status code 400 on malformed request" in {
      val future: Future[Result] = baseFakeRequest(
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
      status(future) mustBe 400
    }

    "return error message 400 on empty title" in {
      val future: Future[Result] = baseFakeRequest(
        Json.obj(
          "operationName" -> "CreateTodo",
          "query"         -> s""" 
                        |mutation CreateTodo {
                        | createTodo(todoToCreate: { title: "", description: ""}) {
                        |   ${todoGQLSchema}
                        | }
                        |}""".stripMargin
        )
      ).get

      // Check Http Status
      status(future) mustBe 200

      // Check content type
      contentType(future) mustBe Some("application/json")

      val content = contentAsJson(future)
      println(content)
      // Check returned content is not of format todo
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

  "Query allTodos" should {
    "return a list of todos" in {
      val future: Future[Result] = baseFakeRequest(
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
      status(future) mustBe 200
      // Check content type
      contentType(future) mustBe Some("application/json")

      val content = contentAsJson(future)
      // Check returned content has format of todo
      content mustEqual Json.obj(
        "data" -> Json.obj(
          "allTodos" -> Json.arr(
            Json.parse(
              s"""
                 |{
                 |   "id": 1,
                 |   "title": "${titleOfTodo}",
                 |   "description": "${descriptionOfTodo}",
                 |   "isDone": false,
                 |   "doneAt": null,
                 |   "createdAt": 1621082427626
                 | }
                 |""".stripMargin
            )
          )
        )
      )
    }
  }
}
