package todorestgraphqlsample.graphql.schema.mutations

import todorestgraphqlsample.graphql.schema.models.Todos.TodoType
import todorestgraphqlsample.graphql.schema.models.Arguments.{ CreateTodoArg, TodoId, UpdateTodoType }
import sangria.schema.Field
import todorestgraphqlsample.repositories.TodoRepository

object Todos {
  val createTodoMutation: Field[TodoRepository, Unit] = Field(
    "createTodo",
    TodoType,
    arguments = CreateTodoArg :: Nil,
    resolve = c => c.ctx.createTodoGraphQl(c arg CreateTodoArg)
  )

  val updateTodoMutation: Field[TodoRepository, Unit] = Field(
    "updateTodo",
    TodoType,
    arguments = UpdateTodoType :: Nil,
    resolve = c => c.ctx.updateTodoGraphQl(c arg UpdateTodoType)
  )

  val deleteTodoMutation: Field[TodoRepository, Unit] = Field(
    "deleteTodo",
    TodoType,
    arguments = TodoId :: Nil,
    resolve = c => c.ctx.deleteGraphQl(c arg TodoId)
  )
}
