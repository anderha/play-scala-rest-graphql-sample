package todorestgraphqlsample.graphql.schema.queries

import todorestgraphqlsample.graphql.schema.models.Todos.TodoType
import sangria.schema.{ Field, ListType }
import todorestgraphqlsample.graphql.schema.models.Arguments.TodoId
import todorestgraphqlsample.repositories.TodoRepository

object Todo {
  val allTodos: Field[TodoRepository, Unit] = Field(
    "allTodos",
    ListType(TodoType),
    arguments = Nil,
    resolve = ctx => ctx.ctx.allGraphQL(),
    description = Some("Query all Todos")
  )

  val todoById: Field[TodoRepository, Unit] = Field(
    "todo",
    TodoType,
    arguments = TodoId :: Nil,
    resolve = ctx => ctx.ctx.lookupGraphQl(ctx arg TodoId),
    description = Some("Query Todo by its ID")
  )
}
