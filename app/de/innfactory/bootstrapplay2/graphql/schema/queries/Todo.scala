package de.innfactory.bootstrapplay2.graphql.schema.queries

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.graphql.schema.models.Todos.TodoType
import sangria.schema.{ Field, ListType }
import de.innfactory.bootstrapplay2.common.implicits.RequestToRequestContextImplicit.EnhancedRequest
import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.bootstrapplay2.graphql.schema.models.Arguments.TodoId
import de.innfactory.grapqhl.play.result.implicits.GraphQlResult.EnhancedFutureResult

object Todo {
  val allTodos: Field[GraphQLExecutionContext, Unit] = Field(
    "allTodos",
    ListType(TodoType),
    arguments = Nil,
    resolve = ctx => {
      ctx.ctx.request.toRequestContextAndExecute(
        "allTodos GraphQL",
        (rc: RequestContext) => ctx.ctx.todoRepository.allGraphQL()(rc)
      )(ctx.ctx.ec)
    },
    description = Some("Query all Todos")
  )

  val todoById: Field[GraphQLExecutionContext, Unit] = Field(
    "todo",
    TodoType,
    arguments = TodoId :: Nil,
    resolve = ctx => ctx.ctx.todoRepository.lookupGraphQl(ctx arg TodoId),
    description = Some("Query Todo by its ID")
  )
}
