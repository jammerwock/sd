package device

import types.DeviceId

import scala.language.higherKinds

trait DeviceRepoAlgebra[F[_]] {
  def retrieveAll: F[List[Device]]
  def retrieveOne(id:DeviceId): F[Option[Device]]
}