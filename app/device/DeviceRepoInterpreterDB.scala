package device

import com.github.mauricio.async.db.ResultSet
import types._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds


class DeviceRepoInterpreterDB(pool: DBConnection)(implicit ec: ExecutionContext) extends DeviceRepoAlgebra[Future] {

  override def retrieveAll: Future[List[Device]] =
    pool.sendQuery(DeviceRepoInterpreterDB.findAll)
      .map(_.rows.map(DeviceRepoInterpreterDB.resultSet2Devices).toList.flatten)

  override def retrieveOne(id: DeviceId): Future[Option[Device]] =
    (pool.sendPreparedStatement _).tupled(DeviceRepoInterpreterDB.findById(id))
      .map(_.rows.flatMap(DeviceRepoInterpreterDB.resultSet2Devices(_).headOption))

  override def retrieveDeviceParams(id: DeviceId): Future[Vector[DeviceParams]] =
    (pool.sendPreparedStatement _).tupled(DeviceRepoInterpreterDB.queryGetParams(id)) map { result =>
      val vector: Option[Vector[DeviceParams]] = result.rows.map { set =>
          val paramses: IndexedSeq[DeviceParams] = set.map { rowData =>
            DeviceParams(
              rowData("param_name").asInstanceOf[String],
              rowData("param_value").asInstanceOf[String],
              rowData("param_order").asInstanceOf[Int]
            )
          }
          paramses.toVector
      }
      vector.get
    }
}


object DeviceRepoInterpreterDB {

  def apply(pool: DBConnection)(implicit ec: ExecutionContext): DeviceRepoInterpreterDB =
    new DeviceRepoInterpreterDB(pool)

  private def queryGetParams(id: DeviceId): (SqlQuery, SqlParams) = (
    "SELECT param_name, param_value, param_order FROM devices_params WHERE device_id = ? ORDER BY param_order ASC", Seq(id)
  )

  private[this] val FIND =
    """
        SELECT
          d.id AS device_id,
          d.name AS device_name,
          d.description AS description,
          d.device_order AS device_order,
          d.category_code,
          d.vendor_code,
          c.id AS category_id,
          c.name AS category_name,
          v.id AS vendor_id,
          v.name AS vendor_name
        FROM devices d
          LEFT JOIN categories c ON c.code = d.category_code
          LEFT JOIN vendors v ON v.code = d.vendor_code
        """.stripMargin

  private val findAll: SqlQuery = FIND

  private def findById(id: DeviceId): (SqlQuery, SqlParams) = (
    FIND + " WHERE d.id = ? ",
    Seq(id)
  )

  private def resultSet2Devices(rs: ResultSet): IndexedSeq[Device] = rs map { rowData =>
    Device(
      rowData("device_id").asInstanceOf[DeviceId],
      rowData("device_name").asInstanceOf[DeviceName],
      Category(
        rowData("category_id").asInstanceOf[CategoryId],
        rowData("category_code").asInstanceOf[CategoryCode],
        rowData("category_name").asInstanceOf[CategoryName]
      ),
      Vendor(
        rowData("vendor_id").asInstanceOf[VendorId],
        rowData("vendor_code").asInstanceOf[VendorCode],
        rowData("vendor_name").asInstanceOf[VendorName]
      ),
      rowData("description").asInstanceOf[String],
      rowData("device_order").asInstanceOf[Int]
    )
  }
}
