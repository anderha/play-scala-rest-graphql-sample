package de.innfactory.bootstrapplay2.repositories

import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import com.google.inject.{ ImplementedBy, Inject }
import de.innfactory.bootstrapplay2.models.api.{ CreateTodo, Todo }
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.Results.ResultStatus
import de.innfactory.bootstrapplay2.common.results.errors.Errors.BadRequest
import de.innfactory.bootstrapplay2.db.TodoDAO
import de.innfactory.bootstrapplay2.graphql.ErrorParserImpl
import de.innfactory.grapqhl.play.result.implicits.GraphQlResult.EnhancedFutureResult
import de.innfactory.play.slick.enhanced.utils.filteroptions.FilterOptions

import scala.Right
import scala.concurrent.{ ExecutionContext, Future }

class TodoRepository @Inject() (todoDAO: TodoDAO)(implicit ec: ExecutionContext, errorParser: ErrorParserImpl)
    extends ImplicitLogContext {
  /*
   *  GRAPHQL-API
   */
  def allGraphQL()(implicit rc: RequestContext): Future[Seq[Todo]] =
    all().completeOrThrow

  def lookupGraphQl(id: Long): Future[Todo] =
    lookup(id).completeOrThrow

  def createTodoGraphQl(todoToCreate: CreateTodo): Future[Todo] =
    post(todoToCreate).completeOrThrow

  def updateTodoGraphQl(todoToUpdate: Todo): Future[Todo] =
    patch(todoToUpdate).completeOrThrow

  def deleteGraphQl(id: Long): Future[Todo] =
    delete(id).completeOrThrow

  /*
   *  REST-API
   */

  def lookup(id: Long): Future[Result[Todo]] = {
    val result = for {
      lookupResult <- EitherT(todoDAO.lookup(id))
    } yield lookupResult
    result.value
  }

  def all(): Future[Result[Seq[Todo]]] = {
    val result = for {
      lookupResult <- EitherT(todoDAO.all().map(_.asRight[ResultStatus]))
    } yield lookupResult
    result.value
  }

  def patch(todo: Todo): Future[Result[Todo]] = {
    val result = for {
      oldTodo     <- EitherT(todoDAO.lookup(todo.id))
      updatedTodo <- EitherT(todoDAO.update(todo.copy(id = oldTodo.id)))
    } yield updatedTodo
    result.value
  }

  def post(todoToCreate: CreateTodo): Future[Result[Todo]] = {
    def validateTodoToCreate: Result[CreateTodo] =
      if (todoToCreate.title.nonEmpty) Right(todoToCreate) else Left(BadRequest())

    val result = for {
      _           <- EitherT(Future(validateTodoToCreate))
      createdTodo <- EitherT(todoDAO.create(todoToCreate))
    } yield createdTodo
    result.value
  }

  def delete(id: Long): Future[Result[Todo]] = {
    val result = for {
      todo <- EitherT(todoDAO.lookup(id))
      _    <- EitherT(todoDAO.delete(id))
    } yield todo
    result.value
  }
}
