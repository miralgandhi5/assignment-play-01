package controllers

import javax.inject.Inject

import models.UserRepo
import models.entities.{ProfileData, User}
import models.forms.UserForms
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class UserController @Inject()(userRepo: UserRepo, userForms: UserForms, cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  /**
    * renders signup form.
    * @return renders signup page with status Ok.
    */
  def signUp(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signUp(userForms.userForm))
  }

  /**
    * renders signin form.
    * @return renders signin page with status Ok.
    */
  def signIn(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signin(userForms.loginForm))
  }

  /**
    * stores user.
    * @return if registered successfully redirects to profile page with session ,
    *         else if form has error than renders signup with badrequest.
    */
  def submitData(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userForms.userForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("user form has error")
        Future.successful(BadRequest(views.html.signUp(formWithErrors)))
      },
      userData => {
        Logger.info("userData>>>>>>>>>>>>> " + userData)
        val newUser = User(userData.firstName, userData.middleName, userData.lastName, userData.username,
          userData.password, userData.mobile, userData.gender, userData.age, userData.hobbies)
        userRepo.store(newUser) map {
          case true => Logger.info("Added>>>>>>>>>>>>> " + userData)
            Ok(views.html.userProfile(userForms.profileForm.fill(ProfileData(userData.firstName, userData.middleName, userData.lastName, userData.password,
              userData.mobile, userData.gender, userData.age, userData.hobbies))))
              .withSession("name" -> newUser.userName, "admin" -> newUser.isAdmin.toString)
              .flashing("registered" -> "success")
          case false => Logger.info("Rejected>>>>>>>>>>>>> " + userData)
            Redirect(routes.HomeController.index())
              .flashing("registered" -> "something went wrong")
        }

      }

    )
  }

  /**
    * enable user to login if userName and password are correct.
    * @return Redirects to userProfile with status.
    *         if failed in login then signin with status badRequest.
    * */
  def login(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      userForms.loginForm.bindFromRequest.fold(
        formWithErrors => {
          Logger.info("login form has error")
          Future.successful(BadRequest(views.html.signin(formWithErrors)))
        },
        loginData => {
          userRepo.getByName(loginData.userName).map {
            case Some(user) if (user.password == loginData.password) && user.isEnabled =>
              Logger.info("login sucessful")
              Redirect(routes.UserController.userProfile()).withSession("name" -> user.userName, "admin" -> user.isAdmin.toString)
            case Some(user) if user.password == loginData.password => Redirect(routes.UserController.signIn()).flashing("login" -> "user id disabled")
            case Some(_) => Redirect(routes.UserController.signIn()).flashing("login" -> "password is incorrect")
            case None => Redirect(routes.UserController.signIn()).flashing("login" -> "user does not exists")
          }
        })
  }

  /**
    * renders forget password.
    * @return forgetPassword with status Ok.
    */
  def forgetPassword: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.forgetPassword(userForms.forgetPasswordForm))
  }

  def logout(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.index()).withNewSession
  }

  /**
    * shows userProfile.
    * @return renders userProfile with status.
    */
  def userProfile: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("name") match {
      case Some(name) => userRepo.getByName(name).map {
        case Some(user) =>
          Logger.info(s"${user.userName} + ${user.isAdmin}")
          Ok(views.html.userProfile(userForms.profileForm.fill(ProfileData(user.firstName, user.middleName, user.lastName, user.password,
            user.mobile, user.gender, user.age, user.hobbies))))
        case None => Redirect(routes.HomeController.error())
      }
      case None => Future.successful(Redirect(routes.HomeController.error()))
    }

  }

  /**
    * updates user.
    * @return renders userProfile after updation with status.
    */

  def updateUser(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userForms.profileForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("user form has error")
        Future.successful(BadRequest(views.html.userProfile(formWithErrors)))
      },
      userData => {
        Logger.info("user updated")
        val profile = ProfileData(userData.firstName, userData.middleName, userData.lastName,
          userData.password, userData.mobileNumber, userData.gender, userData.age, userData.hobbies)
        request.session.get("name") match {
          case Some(name) => userRepo.update(profile, name).map {
            case true =>
              Redirect(routes.UserController.userProfile()).flashing("msg" -> "profile updated")
            case false => Redirect(routes.UserController.userProfile()).flashing("msg" -> "profile not updated")
          }
          case None => Future.successful(Redirect(routes.HomeController.error()))
        }
      })
  }

  /**
    * updates password of user.
    * @return redirects to forget password with status.
    */
  def updatePassword(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Logger.info(s"update Password")
    userForms.forgetPasswordForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("form has error")
        Future.successful(BadRequest(views.html.forgetPassword(formWithErrors)))
      },
      userData => {
        Logger.info(s"passowrd update called")
        userRepo.updatePassword(userData.userName, userData.password) map {
          case true => Redirect(routes.UserController.forgetPassword()).flashing("status" -> "password changed")
          case false => Redirect(routes.UserController.forgetPassword()).flashing("status" -> "password could not be changed")
        }
      }
    )

  }


}

