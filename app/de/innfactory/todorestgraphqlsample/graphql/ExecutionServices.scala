package de.innfactory.todorestgraphqlsample.graphql

import javax.inject.Inject
import de.innfactory.todorestgraphqlsample.repositories.TodoRepository

case class ExecutionServices @Inject() (
  todoRepository: TodoRepository
)
