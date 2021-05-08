package de.innfactory.bootstrapplay2.graphql.schema.queries

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.graphql.schema.queries.Todo.allTodos
import sangria.schema.{ fields, ObjectType }

object QueryDefinition {
  val Query: ObjectType[GraphQLExecutionContext, Unit] = ObjectType(
    name = "Query",
    description = "Familotel API Queries",
    fields = fields[GraphQLExecutionContext, Unit](
      allTodos
    )
  )
}
