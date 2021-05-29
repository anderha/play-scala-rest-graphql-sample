import javax.inject.{ Inject, Provider, Singleton }
import com.typesafe.config.Config
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment, Logger, Mode }
import slick.jdbc.JdbcBackend.Database
import com.google.inject.AbstractModule
import de.innfactory.play.flyway.FlywayMigrator
import play.api.libs.concurrent.AkkaGuiceSupport
import todorestgraphqlsample.db.TodoDAO

import scala.concurrent.Future

/**
 * This module handles the bindings for the API to the Slick implementation.
 *
 * https://www.playframework.com/documentation/latest/ScalaDependencyInjection#Programmatic-bindings
 */
class Module(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {

  val logger = Logger("application")

  override def configure(): Unit = {
    logger.info(s"Configuring ${environment.mode}")

    bind(classOf[Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[FlywayMigratorImpl]).asEagerSingleton()
    bind(classOf[DAOCloseHook]).asEagerSingleton()
  }
}

/** Migrate Flyway on application start */
class FlywayMigratorImpl @Inject() (env: Environment, configuration: Configuration)
    extends FlywayMigrator(configuration, env, configIdentifier = "todo-rest-graphql-sample")

@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[Database] {
  lazy val get = Database.forConfig("todo-rest-graphql-sample.database", config)
}

/** Closes DAO. Important on dev restart. */
class DAOCloseHook @Inject() (todoDAO: TodoDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful({
      todoDAO.close()
    })
  }
}
