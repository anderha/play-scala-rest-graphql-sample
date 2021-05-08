package de.innfactory.bootstrapplay2.graphql.schema.mutations

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.graphql.schema.mutations.Todos.{
  createTodoMutation,
  deleteTodoMutation,
  updateTodoMutation
}
import sangria.schema.{ fields, ObjectType }

object MutationDefinition {

  val Mutation: ObjectType[GraphQLExecutionContext, Unit] = ObjectType(
    name = "mutation",
    description = "API Mutations",
    fields = fields(
      createTodoMutation,
      updateTodoMutation,
      deleteTodoMutation
    )
  )

}
