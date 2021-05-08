package de.innfactory.bootstrapplay2.graphql

import javax.inject.Inject
import de.innfactory.bootstrapplay2.repositories.{ TodoRepository }

case class ExecutionServices @Inject() (
  todoRepository: TodoRepository
)
