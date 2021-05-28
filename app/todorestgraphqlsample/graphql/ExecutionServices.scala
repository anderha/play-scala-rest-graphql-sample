package todorestgraphqlsample.graphql

import javax.inject.Inject
import todorestgraphqlsample.repositories.TodoRepository

case class ExecutionServices @Inject() (
  todoRepository: TodoRepository
)
