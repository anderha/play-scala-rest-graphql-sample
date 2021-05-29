package todorestgraphqlsample.controllers

import play.api.libs.json.{ JsObject, JsString, JsValue, Json }
import play.api.mvc._
import sangria.renderer.{ SchemaFilter, SchemaRenderer }
import todorestgraphqlsample.graphql.schema.SchemaDefinition
import todorestgraphqlsample.graphql.{ ExecutionServices, RequestExecutor }
import todorestgraphqlsample.repositories.TodoRepository

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class GraphQLController @Inject() (
  cc: ControllerComponents,
  todoRepository: TodoRepository,
  requestExecutor: RequestExecutor
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def graphql: Action[AnyContent] =
    Action.async { request =>
      val json: JsValue = request.body.asJson.get // Get the request body as json
      val query         = (json \ "query").as[String]
      val operationName = (json \ "operationName").asOpt[String]
      val variables     = (json \ "variables").toOption.flatMap {
        case JsString(vars) =>
          Some(if (vars.trim == "" || vars.trim == "null") Json.obj() else Json.parse(vars).as[JsObject])
        case obj: JsObject  => Some(obj)
        case _              => None
      }
      requestExecutor.executeQuery(
        query,
        variables,
        operationName,
        todoRepository
      )
    }

  def renderSchema: Action[AnyContent] =
    Action.async { _ =>
      val renderedSchema = SchemaRenderer.renderSchema(
        SchemaDefinition.graphQLSchema,
        SchemaFilter.withoutGraphQLBuiltIn
      )
      Future(Ok(renderedSchema))
    }
}
