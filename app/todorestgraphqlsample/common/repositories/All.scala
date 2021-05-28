package todorestgraphqlsample.common.repositories

import todorestgraphqlsample.common.results.Results.Result
import todorestgraphqlsample.common.request.RequestContext

import scala.concurrent.Future

trait All[RC <: RequestContext, T] {
  def all(implicit rc: RC): Future[Result[Seq[T]]]
}
