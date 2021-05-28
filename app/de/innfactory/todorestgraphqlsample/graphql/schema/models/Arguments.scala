package de.innfactory.todorestgraphqlsample.graphql.schema.models

import de.innfactory.grapqhl.sangria.marshalling.playJson.playJsonReaderFromInput
import de.innfactory.todorestgraphqlsample.graphql.schema.models.Todos.{ CreateTodoType, UpdateTodoType }
import de.innfactory.todorestgraphqlsample.models.api.{ CreateTodo, Todo }
import sangria.schema.{ Argument, LongType, OptionInputType, StringType }

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

  val CreateTodoArg: Argument[CreateTodo] =
    Argument("todoToCreate", CreateTodoType)

  val UpdateTodoType: Argument[Todo] = Argument("todoToUpdate", Todos.UpdateTodoType)
}
