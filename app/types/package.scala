import com.github.mauricio.async.db.pool.ConnectionPool
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import device._

import scala.concurrent.Future

package object types {
  type Query = String
  type Params = Seq[Any]

  type DBConnection = ConnectionPool[PostgreSQLConnection]

  type DeviceId = Long
  type DeviceName = String

  type CategoryId = Long
  type CategoryName = String
  type CategoryCode = String

  type VendorId = Long
  type VendorName = String
  type VendorCode = String

  type FileName = String


  type MenuData = Map[Category, Map[Vendor, List[Device]]]
  type CarouselData = List[CarouselItem]

  type DeviceServiceFuture = DeviceService[Future]
}