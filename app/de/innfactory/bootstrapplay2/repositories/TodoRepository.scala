package de.innfactory.bootstrapplay2.repositories

import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import com.google.inject.{ ImplementedBy, Inject }
import de.innfactory.bootstrapplay2.models.api.{ CreateTodo, Todo }
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.Results.ResultStatus
import de.innfactory.bootstrapplay2.db.TodoDAO
import de.innfactory.bootstrapplay2.graphql.ErrorParserImpl
import de.innfactory.play.slick.enhanced.utils.filteroptions.FilterOptions

import scala.concurrent.{ ExecutionContext, Future }

class TodoRepository @Inject() (todoDAO: TodoDAO)(implicit ec: ExecutionContext, errorParser: ErrorParserImpl)
    extends ImplicitLogContext {
  /*
   *  GRAPHQL-API
   */
  def allGraphQL(
    filter: Seq[FilterOptions[_root_.dbdata.Tables.Todo, _]]
  )(implicit rc: RequestContext): Future[Seq[Todo]] =
    Future {
      Seq()
    }

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
    val result = for {
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
