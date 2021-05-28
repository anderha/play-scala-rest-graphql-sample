package de.innfactory.todorestgraphqlsample.common.validators

import de.innfactory.todorestgraphqlsample.common.results.Results.Result
import de.innfactory.todorestgraphqlsample.common.results.errors.Errors.BadRequest
import play.api.libs.json.{ JsError, JsSuccess, JsValue, Reads }

object JsonValidator {
  implicit class JsValueJsonValidator(jsValue: JsValue) {
    def validateFor[T](implicit reads: Reads[T]): Result[Boolean] =
      jsValue.validate[T] match {
        case JsSuccess(_, _) => Right(true)
        case JsError(_)      => Left(BadRequest())
      }
  }
}
