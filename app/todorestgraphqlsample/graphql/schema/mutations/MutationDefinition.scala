package todorestgraphqlsample.graphql.schema.mutations

import Todos.{ createTodoMutation, deleteTodoMutation, updateTodoMutation }
import sangria.schema.{ fields, ObjectType }
import todorestgraphqlsample.repositories.TodoRepository

object MutationDefinition {

  val Mutation: ObjectType[TodoRepository, Unit] = ObjectType(
    name = "Mutation",
    description = "API Mutations",
    fields = fields(
      createTodoMutation,
      updateTodoMutation,
      deleteTodoMutation
    )
  )

}
