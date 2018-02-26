package device

import types.DeviceId

import scala.language.higherKinds

class DeviceService[F[_]](deviceRepoAlgebra: DeviceRepoAlgebra[F]) {

  def getAll: F[List[Device]] = deviceRepoAlgebra.retrieveAll

  def getOne(id: DeviceId): F[Option[Device]] = deviceRepoAlgebra.retrieveOne(id)

  def getParams(id: DeviceId): F[Vector[DeviceParams]] = deviceRepoAlgebra.retrieveDeviceParams(id)

  def getImages(id: DeviceId): F[Vector[DeviceImage]] = deviceRepoAlgebra.retrieveDeviceImages(id)

}

object DeviceService {
  def apply[F[_]](deviceRepoAlgebra: DeviceRepoAlgebra[F]): DeviceService[F] =
    new DeviceService(deviceRepoAlgebra)
}
