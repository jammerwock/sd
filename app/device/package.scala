import types._

package object device {

  case class Category(id: CategoryId, code: CategoryCode, name: CategoryName)

  case class Vendor(id: VendorId, code: VendorCode, name: VendorName)

  case class Device(id: DeviceId,
                    name: DeviceName,
                    category: Category,
                    vendor: Vendor,
                    description: String,
                    order:Int,
                    params: Vector[DeviceParams] = Vector.empty)

  case class CarouselItem(fileName: FileName)

  case class DeviceParams(name: String, value: String, order: Int)
}
