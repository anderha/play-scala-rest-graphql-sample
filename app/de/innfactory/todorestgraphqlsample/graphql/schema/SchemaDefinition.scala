package de.innfactory.todorestgraphqlsample.graphql.schema

import de.innfactory.grapqhl.sangria.resolvers.generic.CustomRootResolver
import de.innfactory.todorestgraphqlsample.graphql.schema.mutations.MutationDefinition.Mutation
import de.innfactory.todorestgraphqlsample.graphql.schema.queries.QueryDefinition.Query
import de.innfactory.todorestgraphqlsample.graphql.GraphQLExecutionContext
import sangria.execution.deferred.DeferredResolver
import sangria.schema.Schema

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
