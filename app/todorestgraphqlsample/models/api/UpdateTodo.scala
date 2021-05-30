package todorestgraphqlsample.models.api

import play.api.libs.json.{ Json, OFormat }

case class UpdateTodo(id: Long, title: String, description: String, isDone: Boolean)

object UpdateTodo {
  implicit val format: OFormat[UpdateTodo] = Json.format[UpdateTodo]
}
