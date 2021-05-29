package todorestgraphqlsample.db

import cats.data.EitherT
import com.google.inject.{ ImplementedBy, Inject }
import todorestgraphqlsample.common.results.Results.Result
import de.innfactory.play.db.codegen.XPostgresProfile
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ ExecutionContext, Future }
import dbdata.Tables
import todorestgraphqlsample.models.api.Todo.patch
import org.joda.time.DateTime
import todorestgraphqlsample.common.results.errors.Errors.{ BadRequest, DatabaseResult, NotFound }
import todorestgraphqlsample.models.api
import todorestgraphqlsample.models.api.{ CreateTodo, Todo }
import todorestgraphqlsample.common.results.Results.{ ResultStatus }

import javax.inject.Singleton

@ImplementedBy(classOf[TodoDAOImpl])
trait TodoDAO {
  def lookup(id: Long): Future[Result[Todo]]
  def all(): Future[Seq[Todo]]
  def create(todoToCreate: CreateTodo): Future[Result[Todo]]
  def update(todo: Todo): Future[Result[Todo]]
  def delete(id: Long): Future[Result[Todo]]
  def close(): Future[Unit]
}

@Singleton
class TodoDAOImpl @Inject() (db: Database)(implicit ec: ExecutionContext) extends TodoDAO {
  val profile: XPostgresProfile.type = XPostgresProfile
  import profile.api._

  private val queryById = Compiled((id: Rep[Long]) => Tables.Todo.filter(_.id === id))

  def lookup(id: Long): Future[Result[api.Todo]] =
    db.run(queryById(id).result.headOption)
      .map({
        case Some(value) => Right(todoRowToTodoObject(value))
        case None        => Left(NotFound())
      })

  def all(): Future[Seq[api.Todo]] =
    db.run(Tables.Todo.result).map(_.map(todoRowToTodoObject))

  def create(todo: CreateTodo): Future[Result[api.Todo]] = {
    val convertedToTableRow = todoToCreateToTodoRow(todo)
    val result              = for {
      created <- db.run((Tables.Todo returning Tables.Todo) += convertedToTableRow)
      result  <- Future(Right(todoRowToTodoObject(created)))
    } yield result
    result
  }

  def update(todo: api.Todo): Future[Result[api.Todo]] = {
    val result = for {
      existing    <- EitherT[Future, ResultStatus, api.Todo](lookup(todo.id).map({
                       case Left(_)      => Left(BadRequest())
                       case Right(value) => Right(todoRowToTodoObject(value))
                     }))
      patched     <- EitherT[Future, ResultStatus, api.Todo](Future(Right(patch(todo, existing))))
      patchResult <- EitherT[Future, ResultStatus, api.Todo](
                       db.run(
                         queryById(todo.id)
                           .update(todoObjectToTodoRow(patched))
                           .map(resultingId =>
                             if (resultingId != 0) Right(patched)
                             else Left(DatabaseResult("Could not update entity"))
                           )
                       )
                     )
    } yield patchResult
    result.value
  }

  def delete(id: Long): Future[Result[api.Todo]] = {
    val result = for {
      todoToDelete <- lookup(id).map({
                        case Left(_)      => Left(BadRequest())
                        case Right(value) => Right(todoRowToTodoObject(value))
                      })
      _            <- db.run(queryById(id).delete)
                        .map(id =>
                          if (id != 0) Right(true)
                          else Left(DatabaseResult("could not delete entity"))
                        )
    } yield todoToDelete
    result
  }

  def close(): Future[Unit] =
    Future.successful(db.close())

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
