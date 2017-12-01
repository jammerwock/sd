package controllers

import javax.inject.Inject

import play.api.mvc._
import play.api.i18n.{ I18nSupport, Lang }

class Language @Inject() (cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  def switch(lang: String) = Action { implicit request =>
    //todo Validate lang

    val f = request.acceptLanguages
    val lang1 = Lang(lang)
    request.headers.get("referer")
      .fold(Redirect(routes.Application.index()).withLang(lang1))(Redirect(_).withLang(lang1))

  }
}
