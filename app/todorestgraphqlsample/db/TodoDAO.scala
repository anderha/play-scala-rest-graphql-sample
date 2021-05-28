package todorestgraphqlsample.db

import com.google.inject.{ ImplementedBy, Inject }
import todorestgraphqlsample.common.results.Results.Result
import de.innfactory.play.db.codegen.XPostgresProfile
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ ExecutionContext, Future }
import dbdata.Tables
import todorestgraphqlsample.models.api.Todo.patch
import todorestgraphqlsample.models.api
import todorestgraphqlsample.models.api.{ CreateTodo, Todo }
import org.joda.time.DateTime
import todorestgraphqlsample.common.logging.ImplicitLogContext
import todorestgraphqlsample.models.api
import todorestgraphqlsample.models.api.{ CreateTodo, Todo }

import javax.inject.Singleton

@ImplementedBy(classOf[TodoDAOImpl])
trait TodoDAO {
  def lookup(id: Long): Future[Result[Todo]]
  def all(): Future[Seq[Todo]]
  def create(todoToCreate: CreateTodo): Future[Result[Todo]]
  def update(todo: Todo): Future[Result[Todo]]
  def delete(id: Long): Future[Result[Boolean]]
  def close(): Future[Unit]
}

@Singleton
class TodoDAOImpl @Inject() (db: Database)(implicit ec: ExecutionContext)
    extends BaseSlickDAONoTracing(db)
    with TodoDAO
    with ImplicitLogContext {
  override val profile: XPostgresProfile.type = XPostgresProfile
  import profile.api._

  private val queryById = Compiled((id: Rep[Long]) => Tables.Todo.filter(_.id === id))

  def lookup(id: Long): Future[Result[api.Todo]] =
    lookupGeneric(queryById(id).result.headOption)

  def all(): Future[Seq[api.Todo]] =
    lookupSequenceGenericRawSequence(
      Tables.Todo.result
    )

  def create(todo: CreateTodo): Future[Result[api.Todo]] =
    createGeneric(
      todo,
      queryById(todo.id).result.headOption,
      (toCreate: Tables.TodoRow) => (Tables.Todo returning Tables.Todo) += toCreate
    )

  def update(todo: api.Todo): Future[Result[api.Todo]] =
    updateGeneric(
      queryById(todo.id).result.headOption,
      (toPatch: api.Todo) => queryById(todo.id).update(todoObjectToTodoRow(toPatch)),
      (old: api.Todo) => patch(todo, old)
    )

  def delete(id: Long): Future[Result[Boolean]] =
    deleteGeneric(
      queryById(id).result.headOption,
      queryById(id).delete
    )

  implicit private def todoObjectToTodoRow(todoObject: api.Todo): Tables.TodoRow =
    Tables.TodoRow(
      id = todoObject.id,
      title = todoObject.title,
      description = todoObject.description,
      isDone = todoObject.isDone,
      doneAt = if (todoObject.isDone) Some(todoObject.doneAt.getOrElse(new DateTime().getMillis)) else None,
      createdAt = todoObject.createdAt
    )

  implicit private def todoToCreateToTodoRow(todoToCreate: CreateTodo): Tables.TodoRow =
    Tables.TodoRow(
      id = 0,
      title = todoToCreate.title,
      description = todoToCreate.description,
      isDone = false,
      doneAt = None,
      createdAt = new DateTime().getMillis
    )

  implicit private def todoRowToTodoObject(todoRow: Tables.TodoRow): api.Todo =
    api.Todo(
      id = todoRow.id,
      title = todoRow.title,
      description = todoRow.description,
      isDone = todoRow.isDone,
      doneAt = todoRow.doneAt,
      createdAt = todoRow.createdAt
    )
}
