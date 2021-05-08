package de.innfactory.bootstrapplay2.graphql.schema.models

import de.innfactory.bootstrapplay2.models.api.Todo
import sangria.macros.derive.{ deriveObjectType, ReplaceField }
import sangria.schema.{ BooleanType, Field, LongType, ObjectType, StringType }

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
      field = Field(name = "doneAt", fieldType = LongType, resolve = todoContext => todoContext.value.doneAt.get)
    ),
    ReplaceField(
      fieldName = "createdAt",
      field = Field(name = "createdAt", fieldType = LongType, resolve = todoContext => todoContext.value.createdAt)
    )
  )
}
