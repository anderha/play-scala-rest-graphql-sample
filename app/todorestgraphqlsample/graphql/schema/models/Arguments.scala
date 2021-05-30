package todorestgraphqlsample.graphql.schema.models

import de.innfactory.grapqhl.sangria.marshalling.playJson.playJsonReaderFromInput
import todorestgraphqlsample.graphql.schema.models.Todos.{ CreateTodoType, UpdateTodoType }
import sangria.schema.{ Argument, LongType, OptionInputType, StringType }
import todorestgraphqlsample.models.api.{ CreateTodo, Todo, UpdateTodo }

object Arguments {
  val TodoId: Argument[Long] =
    Argument(
      "id",
      LongType
    )

  val CreateTodoArg: Argument[CreateTodo] =
    Argument("todoToCreate", CreateTodoType)

  val UpdateTodoType: Argument[UpdateTodo] = Argument("todoToUpdate", Todos.UpdateTodoType)
}
