package todorestgraphqlsample.models.api

import org.joda.time.DateTime
import play.api.libs.json.{ Format, Json }

case class Todo(id: Long, title: String, description: String, isDone: Boolean, doneAt: Option[Long], createdAt: Long)

object Todo {
  implicit val format: Format[Todo] = Json.format[Todo]

  def patch(newObject: Todo, oldObject: Todo): Todo =
    Todo(
      id = oldObject.id,
      title = newObject.title,
      description = newObject.description,
      isDone = newObject.isDone,
      doneAt =
        if (oldObject.isDone && newObject.isDone) oldObject.doneAt
        else if (newObject.isDone) Some(new DateTime().getMillis)
        else None,
      createdAt = oldObject.createdAt
    )
}
