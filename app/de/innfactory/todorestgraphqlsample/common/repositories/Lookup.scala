package de.innfactory.todorestgraphqlsample.common.repositories

import de.innfactory.todorestgraphqlsample.common.results.Results.Result
import de.innfactory.todorestgraphqlsample.common.request.RequestContext

import scala.concurrent.Future

trait Lookup[ID, RC <: RequestContext, T] {
  def lookup(id: ID)(implicit rc: RC): Future[Result[T]]
}
