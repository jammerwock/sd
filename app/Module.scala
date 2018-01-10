import javax.inject.Singleton

import com.github.mauricio.async.db.pool.{ConnectionPool, PoolConfiguration}
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.google.inject.{AbstractModule, Provides}
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  @Provides
  @Singleton
  def dbPool(lifecycle: ApplicationLifecycle): ConnectionPool[PostgreSQLConnection] = {
    val pool = new ConnectionPool(
      new PostgreSQLConnectionFactory(
        URLParser.parse(configuration.get[String]("appdb.url"))
      ),
      PoolConfiguration.Default
    )
    lifecycle.addStopHook { () => pool.close }
    pool
  }

  override def configure(): Unit = {

  }
}
