package de.innfactory.bootstrapplay2.db

import com.google.inject.{ ImplementedBy, Inject }
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.models.api.{ CreateTodo, Todo => TodoObject }
import de.innfactory.play.db.codegen.XPostgresProfile
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ ExecutionContext, Future }
import dbdata.Tables
import de.innfactory.bootstrapplay2.models.api.Todo.patch
import javax.inject.Singleton

@ImplementedBy(classOf[TodoDAOImpl])
trait TodoDAO {
  def lookup(id: Long): Future[Result[TodoObject]]
  def all(): Future[Seq[TodoObject]]
  def create(todoToCreate: CreateTodo): Future[Result[TodoObject]]
  def update(todo: TodoObject): Future[Result[TodoObject]]
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

  def lookup(id: Long): Future[Result[TodoObject]] =
    lookupGeneric(queryById(id).result.headOption)

  def all(): Future[Seq[TodoObject]] =
    lookupSequenceGenericRawSequence(
      Tables.Todo.result
    )

  def create(todo: CreateTodo): Future[Result[TodoObject]] =
    createGeneric(
      todo,
      queryById(todo.id).result.headOption,
      (toCreate: Tables.TodoRow) => (Tables.Todo returning Tables.Todo) += toCreate
    )

  def update(todo: TodoObject): Future[Result[TodoObject]] =
    updateGeneric(
      queryById(todo.id).result.headOption,
      (toPatch: TodoObject) => queryById(todo.id).update(todoObjectToTodoRow(toPatch)),
      (old: TodoObject) => patch(todo, old)
    )

  def delete(id: Long): Future[Result[Boolean]] =
    deleteGeneric(
      queryById(id).result.headOption,
      queryById(id).delete
    )

  implicit private def todoObjectToTodoRow(todoObject: TodoObject): Tables.TodoRow =
    Tables.TodoRow(
      id = todoObject.id,
      title = todoObject.title,
      description = todoObject.description,
      isDone = todoObject.isDone,
      doneAt = todoObject.doneAt,
      createdAt = todoObject.createdAt
    )

  implicit private def todoToCreateToTodoRow(todoToCreate: CreateTodo): Tables.TodoRow =
    Tables.TodoRow(
      id = 0,
      title = todoToCreate.title,
      description = todoToCreate.description,
      isDone = false,
      doneAt = None,
      createdAt = System.currentTimeMillis
    )

  implicit private def todoRowToTodoObject(todoRow: Tables.TodoRow): TodoObject =
    TodoObject(
      id = todoRow.id,
      title = todoRow.title,
      description = todoRow.description,
      isDone = todoRow.isDone,
      doneAt = todoRow.doneAt,
      createdAt = todoRow.createdAt
    )
}
