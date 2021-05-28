package todorestgraphqlsample.common.request

import de.innfactory.play.tracing.TraceRequest
import io.opencensus.trace.Span
import play.api.mvc.{ AnyContent, Request }
import todorestgraphqlsample.common.request.logger.TraceLogger

trait BaseRequestContext {

  def request: Request[AnyContent]

}

class RequestContext(rcRequest: Request[AnyContent]) extends BaseRequestContext {
  override def request: Request[AnyContent] = rcRequest
}

object ReqConverterHelper {

  def requestContext[R[A] <: TraceRequest[AnyContent]](implicit req: R[_]): RequestContext =
    new RequestContext(req.request)
}
