package de.innfactory.bootstrapplay2.models.api

import play.api.libs.json.{ Format, Json }

case class CreateTodo(title: String, description: String)

object CreateTodo {
  implicit val format: Format[CreateTodo] = Json.format[CreateTodo]
}
