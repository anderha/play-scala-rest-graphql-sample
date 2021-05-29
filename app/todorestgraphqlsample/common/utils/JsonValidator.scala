package todorestgraphqlsample.common.utils

import play.api.libs.json.{ JsError, JsSuccess, JsValue, Reads }
import todorestgraphqlsample.common.results.Results.Result
import todorestgraphqlsample.common.results.errors.Errors.BadRequest

object JsonValidator {
  implicit class JsValueJsonValidator(jsValue: JsValue) {
    def validateFor[T](implicit reads: Reads[T]): Result[Boolean] =
      jsValue.validate[T] match {
        case JsSuccess(_, _) => Right(true)
        case JsError(_)      => Left(BadRequest())
      }
  }
}
