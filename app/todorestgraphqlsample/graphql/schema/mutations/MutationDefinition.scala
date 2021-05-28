package todorestgraphqlsample.graphql.schema.mutations

import Todos.{ createTodoMutation, deleteTodoMutation, updateTodoMutation }
import sangria.schema.{ fields, ObjectType }
import todorestgraphqlsample.graphql.GraphQLExecutionContext

object MutationDefinition {

  val Mutation: ObjectType[GraphQLExecutionContext, Unit] = ObjectType(
    name = "Mutation",
    description = "API Mutations",
    fields = fields(
      createTodoMutation,
      updateTodoMutation,
      deleteTodoMutation
    )
  )

}
