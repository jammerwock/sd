
import javax.inject.Singleton

import com.github.mauricio.async.db.pool.{ConnectionPool, PoolConfiguration}
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.google.inject.{AbstractModule, Provides}
import device.{DeviceRepoInterpreterDB, DeviceService}
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment}
import types.{DBConnection, DeviceServiceFuture}

import scala.concurrent.{ExecutionContext, Future}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {


  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  @Provides
  @Singleton
  def dbPool(lifecycle: ApplicationLifecycle): DBConnection = {
    val pool = new ConnectionPool(
      new PostgreSQLConnectionFactory(URLParser.parse(configuration.get[String]("appdb.url"))),
      PoolConfiguration.Default
    )
    lifecycle.addStopHook { () => pool.close }
    pool
  }

  @Provides
  @Singleton
  def deviceService(pool: DBConnection): DeviceServiceFuture = {
    DeviceService[Future](DeviceRepoInterpreterDB(pool))
  }


  override def configure(): Unit = {

  }
}
