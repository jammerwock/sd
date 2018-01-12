package device

import types.DeviceId

class DeviceService[F[_]](deviceRepoAlgebra: DeviceRepoAlgebra[F]) {

  def getAll: F[List[Device]] = deviceRepoAlgebra.retrieveAll

  def getOne(id: DeviceId): F[Option[Device]] = deviceRepoAlgebra.retrieveOne(id)

}

object DeviceService {
  def apply[F[_]](deviceRepoAlgebra: DeviceRepoAlgebra[F]): DeviceService[F] =
    new DeviceService(deviceRepoAlgebra)
}
