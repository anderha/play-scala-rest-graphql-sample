package todorestgraphqlsample.graphql.schema.queries

import todorestgraphqlsample.graphql.schema.queries.Todo.{ allTodos, todoById }
import sangria.schema.{ fields, ObjectType }
import todorestgraphqlsample.graphql.GraphQLExecutionContext

object QueryDefinition {
  val Query: ObjectType[GraphQLExecutionContext, Unit] = ObjectType(
    name = "Query",
    description = "API Queries",
    fields = fields[GraphQLExecutionContext, Unit](
      allTodos,
      todoById
    )
  )
}
