package todorestgraphqlsample.models.api

import org.joda.time.DateTime
import play.api.libs.json.{ Format, Json }

case class Todo(id: Long, title: String, description: String, isDone: Boolean, doneAt: Option[Long], createdAt: Long)

object Todo {
  implicit val format: Format[Todo] = Json.format[Todo]

  private def determineDoneAt(isDoneOld: Boolean, isDoneNew: Boolean, doneAtOld: Option[Long]): Option[Long] =
    if (isDoneOld && isDoneNew) doneAtOld
    else if (isDoneNew) Some(new DateTime().getMillis)
    else None

  def patch(newObject: UpdateTodo, oldObject: Todo): Todo =
    Todo(
      id = oldObject.id,
      title = newObject.title,
      description = newObject.description,
      isDone = newObject.isDone,
      doneAt = determineDoneAt(oldObject.isDone, newObject.isDone, oldObject.doneAt),
      createdAt = oldObject.createdAt
    )

  def patch(newObject: Todo, oldObject: Todo): Todo =
    Todo(
      id = oldObject.id,
      title = newObject.title,
      description = newObject.description,
      isDone = newObject.isDone,
      doneAt = determineDoneAt(oldObject.isDone, newObject.isDone, oldObject.doneAt),
      createdAt = oldObject.createdAt
    )
}
