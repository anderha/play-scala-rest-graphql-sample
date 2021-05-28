package todorestgraphqlsample.common.repositories

import todorestgraphqlsample.common.results.Results.Result
import todorestgraphqlsample.common.request.RequestContext

import scala.concurrent.Future

trait Post[RC <: RequestContext, T] {
  def post(entity: T)(implicit rc: RC): Future[Result[T]]
}

trait PostWithReturnType[RC <: RequestContext, T, A] {
  def post(entity: T)(implicit rc: RC): Future[Result[A]]
}
