package de.innfactory.bootstrapplay2.graphql.schema.models

import de.innfactory.bootstrapplay2.graphql.schema.models.Todos.{
  CreateTodoType,
  UpdateTodoType => UpdateTodoInputType
}
import de.innfactory.grapqhl.sangria.marshalling.playJson.playJsonReaderFromInput
import sangria.schema.{ Argument, LongType, OptionInputType, StringType }
import sangria.marshalling.FromInput

object Arguments {
  val TodoFilterArg: Argument[Option[String]] =
    Argument(
      "filter",
      OptionInputType(StringType),
      description = "Filters for Todos, separated by & with key=value"
    )

  val TodoId: Argument[Long] =
    Argument(
      "id",
      LongType
    )

  val CreateTodoArg =
    Argument("createTodo", CreateTodoType)

  val UpdateTodoType = Argument("updateTodo", UpdateTodoInputType)
}
