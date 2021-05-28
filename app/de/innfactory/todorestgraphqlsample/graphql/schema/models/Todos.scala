package de.innfactory.todorestgraphqlsample.graphql.schema.models

import de.innfactory.todorestgraphqlsample.models.api.{ CreateTodo, Todo }
import sangria.macros.derive.{ deriveInputObjectType, deriveObjectType, InputObjectTypeName, ReplaceField }
import sangria.schema.{ BooleanType, Field, InputObjectType, LongType, ObjectType, OptionType, StringType }

object Todos {
  val TodoType: ObjectType[Unit, Todo] = deriveObjectType(
    ReplaceField(
      fieldName = "id",
      field = Field(name = "id", fieldType = LongType, resolve = todoContext => todoContext.value.id)
    ),
    ReplaceField(
      fieldName = "title",
      field = Field(name = "title", fieldType = StringType, resolve = todoContext => todoContext.value.title)
    ),
    ReplaceField(
      fieldName = "description",
      field =
        Field(name = "description", fieldType = StringType, resolve = todoContext => todoContext.value.description)
    ),
    ReplaceField(
      fieldName = "isDone",
      field = Field(name = "isDone", fieldType = BooleanType, resolve = todoContext => todoContext.value.isDone)
    ),
    ReplaceField(
      fieldName = "doneAt",
      field =
        Field(name = "doneAt", fieldType = OptionType(LongType), resolve = todoContext => todoContext.value.doneAt)
    ),
    ReplaceField(
      fieldName = "createdAt",
      field = Field(name = "createdAt", fieldType = LongType, resolve = todoContext => todoContext.value.createdAt)
    )
  )

  val CreateTodoType: InputObjectType[CreateTodo] = deriveInputObjectType[CreateTodo](
    InputObjectTypeName("TodoToCreateType")
  )
  val UpdateTodoType: InputObjectType[Todo]       = deriveInputObjectType[Todo](
    InputObjectTypeName("TodoToUpdateType")
  )
}
