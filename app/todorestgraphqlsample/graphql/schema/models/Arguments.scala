package todorestgraphqlsample.graphql.schema.models

import de.innfactory.grapqhl.sangria.marshalling.playJson.playJsonReaderFromInput
import todorestgraphqlsample.graphql.schema.models.Todos.{ CreateTodoType, UpdateTodoType }
import sangria.schema.{ Argument, LongType, OptionInputType, StringType }
import todorestgraphqlsample.models.api.{ CreateTodo, Todo }

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
