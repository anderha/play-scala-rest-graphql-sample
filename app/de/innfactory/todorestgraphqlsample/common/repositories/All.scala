package de.innfactory.todorestgraphqlsample.common.repositories

import de.innfactory.todorestgraphqlsample.common.results.Results.Result
import de.innfactory.todorestgraphqlsample.common.request.RequestContext

import scala.concurrent.Future

trait All[RC <: RequestContext, T] {
  def all(implicit rc: RC): Future[Result[Seq[T]]]
}
