package todorestgraphqlsample.db

import cats.data.EitherT
import dbdata.Tables
import todorestgraphqlsample.common.utils.OptionUtils.EnhancedOption
import de.innfactory.play.db.codegen.XPostgresProfile
import todorestgraphqlsample.common.results.Results.{ Result, ResultStatus }
import todorestgraphqlsample.common.results.errors.Errors.{ BadRequest, DatabaseResult, NotFound }
import slick.dbio.{ DBIOAction, Effect, NoStream }
import slick.jdbc.JdbcBackend.Database
import todorestgraphqlsample.common.logging.ImplicitLogContext
import todorestgraphqlsample.common.results.Results.ResultStatus

import scala.concurrent.{ ExecutionContext, Future }

case class BaseSlickDAONoTracing(db: Database)(implicit ec: ExecutionContext) extends Tables with ImplicitLogContext {
  override val profile = XPostgresProfile

  def lookupGeneric[R, T](
    queryHeadOption: DBIOAction[Option[R], NoStream, Nothing]
  )(implicit rowToObject: R => T): Future[Result[T]] = {
    val queryResult: Future[Option[R]] = db.run(queryHeadOption)
    queryResult.map { res: Option[R] =>
      if (res.isDefined)
        Right(rowToObject(res.get))
      else
        Left(
          NotFound()
        )
    }
  }

  def lookupGenericOption[R, T](
    queryHeadOption: DBIOAction[Option[R], NoStream, Nothing]
  )(implicit rowToObject: R => T): Future[Option[T]] = {
    val queryResult: Future[Option[R]] = db.run(queryHeadOption)
    queryResult.map { res: Option[R] =>
      if (res.isDefined)
        Some(rowToObject(res.get))
      else
        None
    }
  }

  def countGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  ): Future[Result[Int]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq)
    queryResult.map(seq => Right(seq.length))
  }

  def lookupSequenceGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  )(implicit rowToObject: R => T): Future[Result[Seq[T]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq)
    queryResult.map { res: Seq[R] =>
      Right(res.map(rowToObject))
    }
  }

  def lookupSequenceGenericRawSequence[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  )(implicit rowToObject: R => T): Future[Seq[T]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq)
    queryResult.map { res: Seq[R] =>
      res.map(rowToObject)
    }
  }

  def lookupSequenceGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    count: Int
  )(implicit rowToObject: R => T): Future[Result[Seq[T]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq)
    queryResult.map { res: Seq[R] =>
      Right(res.takeRight(count).map(rowToObject))
    }
  }

  def lookupSequenceGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    from: Int,
    to: Int
  )(implicit rowToObject: R => T): Future[Result[Seq[T]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq)
    queryResult.map { res: Seq[R] =>
      Right(res.slice(from, to + 1).map(rowToObject))
    }
  }

  def lookupSequenceGeneric[R, T, X, Z](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    mapping: T => X,
    filter: X => Boolean,
    afterFilterMapping: X => Z
  )(implicit rowToObject: R => T): Future[Result[Seq[Z]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq)
    queryResult.map { res: Seq[R] =>
      Right(res.map(rowToObject).map(mapping).filter(filter).map(afterFilterMapping))
    }
  }

  def lookupSequenceGeneric[R, T, Z](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    sequenceMapping: Seq[T] => Z
  )(implicit rowToObject: R => T): Future[Result[Z]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq)
    queryResult.map { res: Seq[R] =>
      val sequence = res.map(rowToObject)
      Right(sequenceMapping(sequence))
    }
  }

  def updateGeneric[R, T](
    queryById: DBIOAction[Option[R], NoStream, Nothing],
    update: T => DBIOAction[Int, NoStream, Effect.Write],
    patch: T => T
  )(implicit rowToObject: R => T): Future[Result[T]] = {
    val result = for {
      lookup        <- EitherT(db.run(queryById).map(_.toEither(BadRequest())))
      patchedObject <- EitherT(Future(Option(patch(rowToObject(lookup))).toEither(BadRequest())))
      patchResult   <-
        EitherT[Future, ResultStatus, T](
          db.run(update(patchedObject)).map { x =>
            if (x != 0) Right(patchedObject)
            else {
              Left(
                DatabaseResult("Could not update entity")
              )
            }
          }
        )
    } yield patchResult
    result.value
  }

  def createGeneric[R, T, A](
    entity: T,
    queryById: DBIOAction[Option[R], NoStream, Nothing],
    create: R => DBIOAction[R, NoStream, Effect.Write]
  )(implicit rowToObject: R => A, objectToRow: T => R): Future[Result[A]] = {
    val entityToSave = objectToRow(entity)
    val result       = for {
      _             <- db.run(queryById).map(_.toInverseEither(BadRequest()))
      createdObject <- db.run(create(entityToSave))
      res           <- Future(
                         Right(rowToObject(createdObject))
                       )
    } yield res
    result
  }

  def createGeneric[R, T](
    entity: T,
    create: R => DBIOAction[R, NoStream, Effect.Write]
  )(implicit rowToObject: R => T, objectToRow: T => R): Future[Result[T]] = {
    val entityToSave = objectToRow(entity)
    val result       = for {
      createdObject <- db.run(create(entityToSave))
      res           <- Future(
                         Right(rowToObject(createdObject))
                       )
    } yield res
    result
  }

  def deleteGeneric[R, T](
    queryById: DBIOAction[Option[R], NoStream, Nothing],
    delete: DBIOAction[Int, NoStream, Effect.Write]
  ): Future[Result[Boolean]] = {
    val result = for {
      _              <- db.run(queryById).map(_.toEither(BadRequest()))
      dbDeleteResult <- db.run(delete).map { x =>
                          if (x != 0)
                            Right(true)
                          else {
                            Left(
                              DatabaseResult(
                                "could not delete entity"
                              )
                            )
                          }
                        }
    } yield dbDeleteResult
    result
  }

  /**
   * Close dao
   * @return
   */
  def close(): Future[Unit] =
    Future.successful(db.close())
}
