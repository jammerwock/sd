package services


import javax.inject.Inject

import cats.Eval
import cats.effect.IO
import device.DeviceService
import types.MenuData

import scala.concurrent.{ExecutionContext, Future}

class Menu @Inject()(deviceService: DeviceService[Future])(implicit ec: ExecutionContext) {

  def getMenu: IO[MenuData] = {
    IO.fromFuture(Eval.always(deviceService.getAll.map(_.groupBy(model => model.category).map { case (a, b) => a -> b.sortBy(_.order).groupBy(_.vendor) })))
  }
}
