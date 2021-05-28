package de.innfactory.todorestgraphqlsample.graphql

import play.api.mvc.{ AnyContent, Request }
import de.innfactory.todorestgraphqlsample.repositories.TodoRepository

import scala.concurrent.ExecutionContext

case class GraphQLExecutionContext(
  request: Request[AnyContent],
  ec: ExecutionContext,
  todoRepository: TodoRepository
)
