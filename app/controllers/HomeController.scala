package controllers

import javax.inject._

import models.AssignmentRepo
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
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */


  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def error() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.errorPage())
  }

}
