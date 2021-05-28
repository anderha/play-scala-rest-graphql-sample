package todorestgraphqlsample.common.implicits

import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import todorestgraphqlsample.common.results.Results.ResultStatus
import todorestgraphqlsample.common.results.Results.ResultStatus
import io.opencensus.scala.Tracing.traceWithParent
import io.opencensus.trace.Span
import todorestgraphqlsample.common.request.TraceContext
import todorestgraphqlsample.common.results.Results.ResultStatus

import scala.concurrent.{ ExecutionContext, Future }

object FutureTracingImplicits {

  implicit class EnhancedFuture[T](future: Future[T]) {
    def trace(
      string: String
    )(implicit tc: TraceContext, ec: ExecutionContext): Future[T] =
      traceWithParent(string, tc.span) { _ =>
        future
      }
  }

  def TracedT[A](
    string: String
  )(implicit tc: TraceContext, ec: ExecutionContext): EitherT[Future, ResultStatus, Span] =
    EitherT(traceWithParent(string, tc.span) { span =>
      Future(span.asRight[ResultStatus])
    })

}
