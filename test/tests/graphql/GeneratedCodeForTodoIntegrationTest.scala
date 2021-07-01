package tests.graphql

import graphql.codegen.queries.CreateTodo
import graphql.codegen.types.TodoToCreateType
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json.{ JsValue, Json, OFormat }
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{ route, POST }
import testutils.Defaults.{ descriptionOfGraphQLTodo, titleOfGraphQLTodo, todoGQLSchema }
import testutils.TestApplicationFactory
import play.api.test.Helpers._

import scala.concurrent.Future

class GeneratedCodeForTodoIntegrationTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  implicit val todoToCreateFormatter: OFormat[TodoToCreateType]                          = Json.format[TodoToCreateType]
  implicit val createTodoFormatter: OFormat[CreateTodo.CreateTodo]                       = Json.format[CreateTodo.CreateTodo]
  implicit val createTodoVariablesFormatter: OFormat[CreateTodo.Variables]               = Json.format[CreateTodo.Variables]
  implicit val createTodoDataFormatter: OFormat[CreateTodo.Data]                         = Json.format[CreateTodo.Data]
  implicit val graphQLResultCreateTodoFormatter: OFormat[GraphQLResult[CreateTodo.Data]] =
    Json.format[GraphQLResult[CreateTodo.Data]]
  implicit val createTodoQueryFormatter: OFormat[GraphQLQuery[CreateTodo.Variables]]     =
    Json.format[GraphQLQuery[CreateTodo.Variables]]

  case class GraphQLQuery[T](operationName: String, query: String, variables: T) {
    def toJson(implicit formatter: OFormat[GraphQLQuery[T]]): JsValue = Json.toJson(this)
  }
  case class GraphQLResult[T](data: T)

  "Generated Code: A Todo (GraphQL)" must {
    "be creatable with createTodo" in {
      val query                        = GraphQLQuery(
        "CreateTodo",
        CreateTodo.document.source.get,
        CreateTodo.Variables(
          TodoToCreateType(
            title = titleOfGraphQLTodo,
            description = descriptionOfGraphQLTodo
          )
        )
      )
      println(query.toJson)
      val futureResult: Future[Result] = route(
        app,
        FakeRequest(POST, "/graphql").withJsonBody(
          query.toJson
        )
      ).get

      // Check Http Status
      status(futureResult) mustBe 200

      // Check content type
      contentType(futureResult) mustBe Some("application/json")

      val content  = contentAsJson(futureResult)
      val response = content.as[GraphQLResult[CreateTodo.Data]]

      response.data.createTodo.title mustBe titleOfGraphQLTodo
      response.data.createTodo.description mustBe descriptionOfGraphQLTodo
    }
  }
}
