import javax.inject.Singleton

import com.github.mauricio.async.db.pool.{ ConnectionPool, PoolConfiguration }
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.github.mauricio.async.db.{ Configuration => DBConfiguration }
import com.google.inject.{ AbstractModule, Provides }
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment }

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  @Provides
  @Singleton
  def dbPool(lifecycle: ApplicationLifecycle): ConnectionPool[PostgreSQLConnection] = {
    val pool = new ConnectionPool(
      new PostgreSQLConnectionFactory(
        new DBConfiguration(
          username = configuration.get[String]("appdb.username"),
          host = configuration.get[String]("appdb.host"),
          port = configuration.get[Int]("appdb.port"),
          password = configuration.getOptional[String]("appdb.password"),
          database = configuration.getOptional[String]("appdb.database")
        )
      ),
      PoolConfiguration.Default
    )
    lifecycle.addStopHook { () => pool.close }
    pool
  }

  override def configure(): Unit = {

  }
}
