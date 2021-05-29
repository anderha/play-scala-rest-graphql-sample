package todorestgraphqlsample.graphql.schema

import todorestgraphqlsample.graphql.schema.mutations.MutationDefinition.Mutation
import todorestgraphqlsample.graphql.schema.queries.QueryDefinition.Query
import sangria.schema.Schema
import todorestgraphqlsample.repositories.TodoRepository

/**
 * Defines a GraphQL schema for the current project
 */
object SchemaDefinition {

  val graphQLSchema: Schema[TodoRepository, Unit] =
    Schema(
      Query,
      Some(Mutation),
      description = Some(
        "Schema for Bootstrap API "
      )
    )

}
