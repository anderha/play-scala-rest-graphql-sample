package de.innfactory.bootstrapplay2.graphql.schema.models

import sangria.schema.{ Argument, OptionInputType, StringType }

object Arguments {
  val TodoFilterArg: Argument[Option[String]] =
    Argument(
      "filter",
      OptionInputType(StringType),
      description = "Filters for Todos, separated by & with key=value"
    )
}
