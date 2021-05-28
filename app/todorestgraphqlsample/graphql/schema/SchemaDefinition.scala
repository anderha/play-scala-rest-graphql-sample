package todorestgraphqlsample.graphql.schema

import de.innfactory.grapqhl.sangria.resolvers.generic.CustomRootResolver
import todorestgraphqlsample.graphql.schema.mutations.MutationDefinition.Mutation
import todorestgraphqlsample.graphql.schema.queries.QueryDefinition.Query
import sangria.execution.deferred.DeferredResolver
import sangria.schema.Schema
import todorestgraphqlsample.graphql.GraphQLExecutionContext

/**
 * Defines a GraphQL schema for the current project
 */
object SchemaDefinition {

  val graphQLSchema: Schema[GraphQLExecutionContext, Unit] =
    Schema(
      Query,
      Some(Mutation),
      description = Some(
        "Schema for Bootstrap API "
      )
    )

}
