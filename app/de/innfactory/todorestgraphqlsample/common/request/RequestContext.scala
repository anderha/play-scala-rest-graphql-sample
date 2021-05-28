package de.innfactory.todorestgraphqlsample.common.request

import de.innfactory.play.tracing.TraceRequest
import de.innfactory.todorestgraphqlsample.common.request.logger.TraceLogger
import io.opencensus.trace.Span
import play.api.mvc.{ AnyContent, Request }

class TraceContext(traceSpan: Span) {
  def span: Span = traceSpan

  private val traceLogger = new TraceLogger(span)

  final def log: TraceLogger = traceLogger
}

trait BaseRequestContext {

  def request: Request[AnyContent]

}

class RequestContext(rcSpan: Span, rcRequest: Request[AnyContent])
    extends TraceContext(rcSpan)
    with BaseRequestContext {
  override def request: Request[AnyContent] = rcRequest
}

object ReqConverterHelper {

  def requestContext[R[A] <: TraceRequest[AnyContent]](implicit req: R[_]): RequestContext =
    new RequestContext(req.traceSpan, req.request)
}
