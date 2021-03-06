package testutils

import com.google.inject.Inject
import de.innfactory.play.flyway.test.TestFlywayMigrator
import org.joda.time.DateTimeUtils
import org.scalatestplus.play.FakeApplicationFactory
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{ Binding, Module }
import play.api.{ Application, Configuration, Environment }

/**
 * Set up an application factory that runs flyways migrations.
 */
trait TestApplicationFactory extends FakeApplicationFactory {

  DateTimeUtils.setCurrentMillisFixed(1621082427626L)

  def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .bindings(new FlywayModule)
      .build()
}

class FlywayModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(bind[FlywayMigrator].toSelf.eagerly())
}

class FlywayMigrator @Inject() (env: Environment, configuration: Configuration)
    extends TestFlywayMigrator(configuration, env)
