package de.innfactory.bootstrapplay2.graphql.schema.mutations

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.graphql.schema.models.Arguments.{ CreateTodoArg, TodoId, UpdateTodoType }
import de.innfactory.bootstrapplay2.graphql.schema.models.Todos.TodoType
import sangria.schema.Field

object Todos {
  val createTodoMutation: Field[GraphQLExecutionContext, Unit] = Field(
    "createTodo",
    TodoType,
    arguments = CreateTodoArg :: Nil,
    resolve = c => c.ctx.todoRepository.createTodoGraphQl(c arg CreateTodoArg)
  )

  val updateTodoMutation: Field[GraphQLExecutionContext, Unit] = Field(
    "updateTodo",
    TodoType,
    arguments = UpdateTodoType :: Nil,
    resolve = c => c.ctx.todoRepository.updateTodoGraphQl(c arg UpdateTodoType)
  )

  val deleteTodoMutation: Field[GraphQLExecutionContext, Unit] = Field(
    "deleteTodo",
    TodoType,
    arguments = TodoId :: Nil,
    resolve = c => c.ctx.todoRepository.deleteGraphQl(c arg TodoId)
  )
}
