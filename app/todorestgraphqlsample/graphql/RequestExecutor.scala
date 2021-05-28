package todorestgraphqlsample.graphql

import de.innfactory.grapqhl.play.request.RequestExecutionBase
import play.api.mvc.{ AnyContent, Request }
import todorestgraphqlsample.graphql.schema.SchemaDefinition

import scala.concurrent.ExecutionContext

class RequestExecutor
    extends RequestExecutionBase[GraphQLExecutionContext, ExecutionServices](SchemaDefinition.graphQLSchema) {
  override def contextBuilder(services: ExecutionServices, request: Request[AnyContent])(implicit
    ec: ExecutionContext
  ): GraphQLExecutionContext =
    GraphQLExecutionContext(
      request = request,
      ec = ec,
      todoRepository = services.todoRepository
    )
}
