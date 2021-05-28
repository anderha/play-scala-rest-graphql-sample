package todorestgraphqlsample.graphql

import todorestgraphqlsample.common.results.Results.ResultStatus
import todorestgraphqlsample.common.results.errors.Errors.{ BadRequest, Forbidden }
import de.innfactory.grapqhl.play.result.implicits.GraphQlResult.{ BadRequestError, ForbiddenError }
import de.innfactory.grapqhl.play.result.implicits.{ ErrorParser, GraphQlException }
import todorestgraphqlsample.common.results.Results

class ErrorParserImpl extends ErrorParser[ResultStatus] {
  override def internalErrorToUserFacingError(error: ResultStatus): GraphQlException = error match {
    case _: BadRequest => BadRequestError("Bad Request")
    case _: Forbidden  => ForbiddenError("Forbidden")
    case _             => BadRequestError("Bad Request")
  }
}
