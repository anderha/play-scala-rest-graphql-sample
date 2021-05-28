package de.innfactory.todorestgraphqlsample.graphql

import de.innfactory.grapqhl.play.request.RequestExecutionBase
import de.innfactory.todorestgraphqlsample.graphql.schema.SchemaDefinition
import play.api.mvc.{ AnyContent, Request }

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
