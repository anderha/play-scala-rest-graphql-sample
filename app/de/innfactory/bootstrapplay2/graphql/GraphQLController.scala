package de.innfactory.bootstrapplay2.graphql

import de.innfactory.bootstrapplay2.graphql.schema.SchemaDefinition
import de.innfactory.grapqhl.play.controller.GraphQLControllerBase
import de.innfactory.grapqhl.play.request.common.ExecutionHelperBase
import de.innfactory.grapqhl.play.request.implicits.JsValueParser.GraphQLBodyEnhancedJsValue
import play.api.libs.json.JsValue

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import sangria.renderer.{ SchemaFilter, SchemaRenderer }

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class GraphQLController @Inject() (
  cc: ControllerComponents,
  executionServices: ExecutionServices,
  requestExecutor: RequestExecutor,
  executionHelper: ExecutionHelperBase = new ExecutionHelperBase()
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def graphql: Action[AnyContent] =
    Action.async { request ⇒
      val json: JsValue = request.body.asJson.get // Get the request body as json
      val graphQLBody   = json.getGraphQLBodyParameters(executionHelper)
      requestExecutor.executeQuery(
        graphQLBody.query,
        graphQLBody.variables,
        graphQLBody.operation,
        executionHelper.isTracingEnabled(request),
        request,
        executionServices
      )
    }

  def renderSchema: Action[AnyContent] =
    Action.async { request =>
      val renderedSchema = SchemaRenderer.renderSchema(
        SchemaDefinition.graphQLSchema,
        SchemaFilter.withoutGraphQLBuiltIn
      )
      Future(Ok(renderedSchema))
    }
}
