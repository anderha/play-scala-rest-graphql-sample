package de.innfactory.todorestgraphqlsample.common.utils

import de.innfactory.todorestgraphqlsample.common.results.Results.{ Result, ResultStatus }

object OptionUtils {
  implicit class EnhancedOption[T](value: Option[T]) {
    def getOrElseOld(oldOption: Option[T]): Option[T] =
      value match {
        case Some(of) => Some(of)
        case None     => oldOption
      }

    def toEither(leftResult: ResultStatus): Result[T] =
      value match {
        case Some(v) => Right(v)
        case None    => Left(leftResult)
      }

    def toInverseEither[X](leftResult: X): Either[X, String] =
      value match {
        case Some(_) => Left(leftResult)
        case None    => Right("")
      }
  }
}
