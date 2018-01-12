//package device
//
//import javax.inject.{Inject, Singleton}
//
//import cats.Eval
//import cats.effect.IO
//import com.github.mauricio.async.db.ResultSet
//import types._
//
//import scala.concurrent.ExecutionContext
//
//
//@Singleton
//@deprecated
//class DeviceDAO @Inject()(pool: DBConnection)(implicit ec: ExecutionContext) {
//
//  private[devices] def findAll(): IO[List[Device]] = IO.fromFuture(Eval.always {
//    pool.sendQuery(DeviceDAO.findAll) map (_.rows.map(DeviceDAO.resultSet2Devices).toList.flatten)
//  })
//
//  private[devices] def findById(id: DeviceId): IO[Option[Device]] = IO.fromFuture(Eval.always {
//    (pool.sendPreparedStatement _).tupled(DeviceDAO.findById(id)) map (_.rows.flatMap(DeviceDAO.resultSet2Devices(_).headOption))
//  })
//}
//
//object DeviceDAO {
//
//  private[this] val FIND =
//    """
//        SELECT
//          m.id AS device_id,
//          m.name AS device_name,
//          m.category_code,
//          m.vendor_code,
//          c.id AS category_id,
//          c.name AS category_name,
//          v.id AS vendor_id,
//          v.name AS vendor_name
//        FROM devices m
//          LEFT JOIN categories c ON c.code = m.category_code
//          LEFT JOIN vendors v ON v.code = m.vendor_code
//        """.stripMargin
//
//  private val findAll: Query = FIND
//
//  private def findById(id: DeviceId): (Query, Params) = (
//    FIND + " WHERE m.id = ? ",
//    Seq(id)
//  )
//
//  private def resultSet2Devices(rs: ResultSet): IndexedSeq[Device] = rs map { rowData =>
//    Device(
//      rowData("device_id").asInstanceOf[DeviceId],
//      rowData("device_name").asInstanceOf[DeviceName],
//      Category(
//        rowData("category_id").asInstanceOf[CategoryId],
//        rowData("category_code").asInstanceOf[CategoryCode],
//        rowData("category_name").asInstanceOf[CategoryName]
//      ),
//      Vendor(
//        rowData("vendor_id").asInstanceOf[VendorId],
//        rowData("vendor_code").asInstanceOf[VendorCode],
//        rowData("vendor_name").asInstanceOf[VendorName]
//      )
//    )
//  }
//
//}
