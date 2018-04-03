package controllers

import javax.inject.Inject

import models.UserRepo
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

class AdminController @Inject()(userRepo: UserRepo, cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {
  /**
    * displays list of users.
    *
    * @return renders viewUsers with Ok status.
    */
  def viewUsers: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userRepo.getAllUsers map (users => Ok(views.html.viewUsers(users)))
  }

  /**
    * enables a user with given username.
    *
    * @param userName username of user.
    * @return Redirects to viewUsers with flash showing status.
    */

  def enableUser(userName: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userRepo.enabledUser(userName) map {
      case true => Redirect(routes.AdminController.viewUsers()).flashing("status" -> "user enabled")
      case false => Redirect(routes.AdminController.viewUsers()).flashing("status" -> "user could not be enabled")
    }
  }


  /**
    * disables a user with given username.
    *
    * @param userName username of user.
    * @return Redirect to viewUsers with flash showing status.
    */

  def disableUser(userName: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userRepo.disabledUser(userName) map {
      case true => Redirect(routes.AdminController.viewUsers()).flashing("status" -> "user disabled")
      case false => Redirect(routes.AdminController.viewUsers()).flashing("status" -> "user could not be disabled")
    }
  }
}
