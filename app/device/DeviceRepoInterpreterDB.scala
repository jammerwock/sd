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
}


object DeviceRepoInterpreterDB {

  def apply(pool: DBConnection)(implicit ec: ExecutionContext): DeviceRepoInterpreterDB =
    new DeviceRepoInterpreterDB(pool)

  private[this] val FIND =
    """
        SELECT
          m.id AS device_id,
          m.name AS device_name,
          m.category_code,
          m.vendor_code,
          c.id AS category_id,
          c.name AS category_name,
          v.id AS vendor_id,
          v.name AS vendor_name
        FROM devices m
          LEFT JOIN categories c ON c.code = m.category_code
          LEFT JOIN vendors v ON v.code = m.vendor_code
        """.stripMargin

  private val findAll: Query = FIND

  private def findById(id: DeviceId): (Query, Params) = (
    FIND + " WHERE m.id = ? ",
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
      )
    )
  }
}
