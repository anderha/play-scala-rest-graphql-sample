package todorestgraphqlsample.common.repositories

import todorestgraphqlsample.common.results.Results.Result
import todorestgraphqlsample.common.request.RequestContext

import scala.concurrent.Future

trait Delete[ID, RC <: RequestContext, T] {
  def delete(id: ID)(implicit rc: RC): Future[Result[T]]
}
