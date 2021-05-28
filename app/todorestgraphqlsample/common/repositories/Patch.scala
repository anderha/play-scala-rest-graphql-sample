package todorestgraphqlsample.common.repositories

import todorestgraphqlsample.common.results.Results.Result
import todorestgraphqlsample.common.request.RequestContext

import scala.concurrent.Future

trait Patch[RC <: RequestContext, T] {
  def patch(entity: T)(implicit rc: RC): Future[Result[T]]
}
