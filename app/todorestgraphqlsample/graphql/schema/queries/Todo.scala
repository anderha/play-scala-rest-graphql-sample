package todorestgraphqlsample.graphql.schema.queries

import todorestgraphqlsample.graphql.schema.models.Todos.TodoType
import sangria.schema.{ Field, ListType }
import todorestgraphqlsample.graphql.schema.models.Arguments.TodoId
import todorestgraphqlsample.graphql.GraphQLExecutionContext

object Todo {
  val allTodos: Field[GraphQLExecutionContext, Unit] = Field(
    "allTodos",
    ListType(TodoType),
    arguments = Nil,
    resolve = ctx => ctx.ctx.todoRepository.allGraphQL(),
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
