package controllers

import javax.inject.Inject

import models.UserRepo
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

class AdminController @Inject()(userRepo: UserRepo, cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {
  def viewUsers: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userRepo.getAllUsers map (users => Ok(views.html.viewUsers(users)))
  }

  def enableUser(userName: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userRepo.enabledUser(userName) map {
      case true => Redirect(routes.AdminController.viewUsers()).flashing("status" -> "user enabled")
      case false => Redirect(routes.AdminController.viewUsers()).flashing("status" -> "user could not be enabled")
    }
  }

  def disableUser(userName: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userRepo.disabledUser(userName) map {
      case true => Redirect(routes.AdminController.viewUsers()).flashing("status" -> "user disabled")
      case false => Redirect(routes.AdminController.viewUsers()).flashing("status" -> "user could not be disabled")
    }
  }
}
