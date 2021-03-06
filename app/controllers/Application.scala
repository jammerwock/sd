package controllers

import javax.inject.Inject
import cats.Eval
import cats.effect.IO
import device.DeviceImage
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{Carousel, Menu}
import types._

import scala.concurrent.ExecutionContext

class Application @Inject()(pool: DBConnection,
                            deviceService: DeviceServiceFuture,
                            menu: Menu,
                            carousel: Carousel,
                            mcc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(mcc) with I18nSupport {

  val menuData: IO[MenuData] = menu.getMenu

  def index: Action[AnyContent] = Action.async { implicit request =>

    val eventualMenuData = menuData
    val eventualCarouselData = carousel.getCarouselData
    (for {
      menuData <- eventualMenuData
      carouselData <- IO.fromFuture(Eval.always(eventualCarouselData))
    } yield Ok(views.html.index(menuData, carouselData))).unsafeToFuture()



  }

  def showItem(categoryCode: String, vendorCode: String, itemId: Long): Action[AnyContent] = Action.async { implicit request =>
    (for {
      menu <- menuData
      device <- IO.fromFuture(Eval.always(deviceService.getOne(itemId)))
      params <- IO.fromFuture(Eval.always(deviceService.getParams(itemId)))
      images <- IO.fromFuture(Eval.always(deviceService.getImages(itemId)))
    } yield {
      val imgs =  if (images.isEmpty) Vector.fill(1)(DeviceImage.NOT_FOUND) else images
      device.fold(Ok(views.html.devices.notFound(request.messages("device.notFound"), menu))) { d =>
        Ok(views.html.devices.found(request.messages("device.device") + " " + d.name, d.copy(params = params.sortBy(_.order)), imgs, menu))
      }
    }).unsafeToFuture()
  }

  def about: Action[AnyContent] = Action.async{ implicit request =>
    (for {
      menuData <- menuData
    } yield
      Ok(views.html.about(request.messages("about.about"), menuData))).unsafeToFuture()
  }
  def contacts: Action[AnyContent] = Action.async{ implicit request =>
    (for {
      menuData <- menuData
    } yield
      Ok(views.html.contacts(request.messages("contacts.contacts"), menuData))).unsafeToFuture()
  }
}
