package todorestgraphqlsample.graphql

import de.innfactory.grapqhl.play.exception.ExceptionHandling
import de.innfactory.grapqhl.play.exception.ExceptionHandling.TooComplexQueryError
import play.api.libs.json.{ JsObject, Json }
import play.api.mvc.Results.{ BadRequest, InternalServerError, Ok }
import play.api.mvc.{ AnyContent, Request, Result }
import sangria.execution.{ ErrorWithResolver, Executor, QueryAnalysisError, QueryReducer }
import sangria.parser.{ QueryParser, SyntaxError }
import todorestgraphqlsample.graphql.schema.SchemaDefinition
import de.innfactory.grapqhl.sangria.marshalling.playJson._
import todorestgraphqlsample.repositories.TodoRepository

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

class RequestExecutor {
  def contextBuilder(services: ExecutionServices, request: Request[AnyContent])(implicit
    ec: ExecutionContext
  ): GraphQLExecutionContext =
    GraphQLExecutionContext(
      request = request,
      ec = ec,
      todoRepository = services.todoRepository
    )

  // Query Reducers Max Depth Of Request
  val queryReducerMaxDepth: Int            = 15
  // Query Reducers Max Complexity For Request
  val queryReducerComplexityThreshold: Int = 4000

  // Predefined Query Reducers
  val baseQueryReducers: List[QueryReducer[Any, _]] = List(
    QueryReducer.rejectMaxDepth[Any](queryReducerMaxDepth),
    QueryReducer.rejectComplexQueries[Any](queryReducerComplexityThreshold, (_, _) => TooComplexQueryError)
  )

  def handleSyntaxError(error: SyntaxError): Future[Result] =
    Future.successful(
      BadRequest(
        Json.obj(
          "syntaxError" -> error.getMessage,
          "locations"   -> Json.arr(
            Json.obj("line" -> error.originalError.position.line, "column" -> error.originalError.position.column)
          )
        )
      )
    )

  def executeQuery(
    query: String,
    variables: Option[JsObject],
    operationName: Option[String],
    todoRepository: TodoRepository
  )(implicit ec: ExecutionContext): Future[Result] =
    QueryParser.parse(query) match {
      case Success(queryAst)           =>
        Executor
          .execute[TodoRepository, Unit, JsObject](
            SchemaDefinition.graphQLSchema,
            queryAst,
            todoRepository,
            operationName = operationName,
            variables = variables.getOrElse(Json.obj()),
            exceptionHandler = ExceptionHandling.exceptionHandler,
            queryReducers = baseQueryReducers
          )
          .map(Ok(_))
          .recover {
            case error: QueryAnalysisError => BadRequest(error.resolveError)
            case error: ErrorWithResolver  => InternalServerError(error.resolveError)
          }
      case Failure(error: SyntaxError) => handleSyntaxError(error)
      case Failure(_)                  => Future(BadRequest(""))
    }
}
