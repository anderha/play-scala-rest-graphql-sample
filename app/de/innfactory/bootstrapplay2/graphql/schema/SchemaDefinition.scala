package de.innfactory.bootstrapplay2.graphql.schema

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.grapqhl.sangria.resolvers.generic.CustomRootResolver
import de.innfactory.bootstrapplay2.graphql.schema.mutations.MutationDefinition.Mutation
import de.innfactory.bootstrapplay2.graphql.schema.queries.QueryDefinition.Query
import sangria.execution.deferred.DeferredResolver
import sangria.schema.{ Schema }

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
