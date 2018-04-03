package controllers

import javax.inject._

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */


@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  implicit val messages: MessagesApi = cc.messagesApi

  /**
    * renders home page.
    * @return index page with status Ok.
    */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  /**
    * renders error page.
    * @return error page with status Ok.
    */
  def error(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.errorPage())
  }

}
