package de.innfactory.todorestgraphqlsample.graphql

import de.innfactory.todorestgraphqlsample.common.results.Results.ResultStatus
import de.innfactory.todorestgraphqlsample.common.results.errors.Errors.{ BadRequest, Forbidden }
import de.innfactory.grapqhl.play.result.implicits.GraphQlResult.{ BadRequestError, ForbiddenError }
import de.innfactory.grapqhl.play.result.implicits.{ ErrorParser, GraphQlException }
import de.innfactory.todorestgraphqlsample.common.results.Results

class ErrorParserImpl extends ErrorParser[ResultStatus] {
  override def internalErrorToUserFacingError(error: ResultStatus): GraphQlException = error match {
    case _: BadRequest => BadRequestError("Bad Request")
    case _: Forbidden  => ForbiddenError("Forbidden")
    case _             => BadRequestError("Bad Request")
  }
}
