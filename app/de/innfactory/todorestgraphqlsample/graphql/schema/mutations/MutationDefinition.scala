package de.innfactory.todorestgraphqlsample.graphql.schema.mutations

import Todos.{ createTodoMutation, deleteTodoMutation, updateTodoMutation }
import de.innfactory.todorestgraphqlsample.graphql.GraphQLExecutionContext
import sangria.schema.{ fields, ObjectType }

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
