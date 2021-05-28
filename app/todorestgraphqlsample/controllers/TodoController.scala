package todorestgraphqlsample.controllers

import cats.data.EitherT
import play.api.mvc.{ AbstractController, Action, AnyContent, ControllerComponents }
import todorestgraphqlsample.common.validators.JsonValidator.JsValueJsonValidator
import cats.implicits._
import todorestgraphqlsample.common.results.Results.Result
import todorestgraphqlsample.models.api.{ CreateTodo, Todo }
import play.api.libs.json.JsValue
import todorestgraphqlsample.models.api.{ CreateTodo, Todo }
import todorestgraphqlsample.repositories.TodoRepository

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class TodoController @Inject() (cc: ControllerComponents, todoRepository: TodoRepository)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def getAll: Action[AnyContent] =
    Action.async {
      val result = todoRepository.all()
      result.completeResult
    }

  def getById(id: Long): Action[AnyContent] =
    Action.async {
      todoRepository.lookup(id).completeResult()
    }

  def post(): Action[AnyContent] =
    Action.async { request =>
      val json = request.body.asJson.get

      val result = for {
        _            <- EitherT(Future(json.validateFor[CreateTodo]))
        todoToCreate <- EitherT(Future(jsonAsCreateTodo(json)))
        created      <- EitherT(todoRepository.post(todoToCreate))
      } yield created

      result.value.completeResult()
    }

  def patch: Action[AnyContent] =
    Action.async { request =>
      val json = request.body.asJson.get

      val result = for {
        _            <- EitherT(Future(json.validateFor[Todo]))
        todoToUpdate <- EitherT(Future(jsonAsTodo(json)))
        updated      <- EitherT(todoRepository.patch(todoToUpdate))
      } yield updated
      result.value.completeResult()
    }

  def delete(id: Int): Action[AnyContent] =
    Action.async {
      todoRepository.delete(id).completeResult()
    }

  private def jsonAsCreateTodo(json: JsValue): Result[CreateTodo] = Right(json.as[CreateTodo])
  private def jsonAsTodo(json: JsValue): Result[Todo]             = Right(json.as[Todo])
}
