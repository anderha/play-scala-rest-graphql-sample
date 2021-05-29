package todorestgraphqlsample.graphql.schema.queries

import todorestgraphqlsample.graphql.schema.queries.Todo.{ allTodos, todoById }
import sangria.schema.{ fields, ObjectType }
import todorestgraphqlsample.repositories.TodoRepository

object QueryDefinition {
  val Query: ObjectType[TodoRepository, Unit] = ObjectType(
    name = "Query",
    description = "API Queries",
    fields = fields[TodoRepository, Unit](
      allTodos,
      todoById
    )
  )
}
