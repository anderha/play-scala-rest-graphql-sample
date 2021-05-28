package de.innfactory.todorestgraphqlsample.common.repositories

import de.innfactory.todorestgraphqlsample.common.results.Results.Result
import de.innfactory.todorestgraphqlsample.common.request.RequestContext

import scala.concurrent.Future

trait Patch[RC <: RequestContext, T] {
  def patch(entity: T)(implicit rc: RC): Future[Result[T]]
}
