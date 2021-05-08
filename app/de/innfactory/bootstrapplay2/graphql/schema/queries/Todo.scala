package de.innfactory.bootstrapplay2.graphql.schema.queries

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.graphql.schema.models.Arguments.TodoFilterArg
import de.innfactory.bootstrapplay2.graphql.schema.models.Todos.TodoType
import sangria.schema.{ Field, ListType }
import de.innfactory.bootstrapplay2.common.implicits.RequestToRequestContextImplicit.EnhancedRequest

object Todo {
  val allTodos: Field[GraphQLExecutionContext, Unit] = Field(
    "allTodos",
    ListType(TodoType),
    arguments = TodoFilterArg :: Nil,
    resolve = ctx => Seq(),
    description = Some("Bootstrap Filter API todos query")
  )
}
