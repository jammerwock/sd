package services

import javax.inject.Inject

import device.CarouselItem
import types.DBConnection

import scala.concurrent.{ExecutionContext, Future}

class Carousel @Inject()(pool: DBConnection)(implicit ec: ExecutionContext) {

  def getCarouselData: Future[List[CarouselItem]] = pool.sendQuery(Carousel.SELECT) map {
    _.rows.map(_.map(rowData =>
      CarouselItem(rowData("filename").asInstanceOf[String]))).get.toList
  }
}

object Carousel {
  private val SELECT = "SELECT filename FROM carousel"
}
