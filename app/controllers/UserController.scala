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

case class UserData(firstName: String, middleName: Option[String], lastName: String, username: String, password: String, confirmPassword: String, mobile: String, gender: String, age: Int, hobbies: String)

class UserController @Inject()(userRepo: UserRepo, userForms: UserForms, cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {


  def signUp() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signUp(userForms.userForm))
  }

  def signIn() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signin(userForms.loginForm))
  }

  def submitData(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userForms.userForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("user form has error")
        Future.successful(BadRequest(views.html.signUp(formWithErrors)))
      },
      userData => {
        Logger.info("userData>>>>>>>>>>>>> " + userData)
        val newUser = User(userData.firstName, userData.middleName, userData.lastName, userData.username, userData.password, userData.mobile, userData.gender, userData.age, userData.hobbies)
        userRepo.store(newUser) map {
          case true => println("Added>>>>>>>>>>>>> " + userData)
            Ok(views.html.userProfile(userForms.profileForm.fill(ProfileData(userData.firstName, userData.middleName, userData.lastName, userData.password, userData.mobile, userData.gender, userData.age, userData.hobbies))))
              .withSession("name" -> newUser.userName, "admin" -> newUser.isAdmin.toString).flashing("registered" -> "success")
          case false => println("Rejected>>>>>>>>>>>>> " + userData)
            Redirect(routes.HomeController.index())
              .flashing("registered" -> "something went wrong")
        }

      }

    )
  }

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
            case Some(user) if user.password == loginData.password  => Redirect(routes.UserController.signIn()).flashing("login" -> "user id disabled")
            case Some(_) =>  Redirect(routes.UserController.signIn()).flashing("login" -> "password is incorrect")
            case None  => Redirect(routes.UserController.signIn()).flashing("login" -> "user does not exists")
          }
        })
  }

  def forgetPassword: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.forgetPassword(userForms.forgetPasswordForm))
  }

  def logout(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.index()).withNewSession
  }

  def userProfile: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("name") match {
      case Some(name) => userRepo.getByName(name).map {
        case Some(user) =>
          Logger.info(s"${user.userName} + ${user.isAdmin}")
          Ok(views.html.userProfile(userForms.profileForm.fill(ProfileData(user.firstName, user.middleName, user.lastName, user.password, user.mobile, user.gender, user.age, user.hobbies))))
        case None => Redirect(routes.HomeController.error())
      }
      case None => Future.successful(Redirect(routes.HomeController.error()))
    }

  }

  def updateUser(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userForms.profileForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("user form has error")
        Future.successful(BadRequest(views.html.userProfile(formWithErrors)))
      },
      userData => {
        Logger.info("userData>>>>>>>>>>>>> " + userData)
        val profile = ProfileData(userData.firstName, userData.middleName, userData.lastName,userData.password,userData.mobileNumber, userData.gender, userData.age, userData.hobbies)
        request.session.get("name") match {
          case Some(name) => userRepo.update(profile,name).map {
            case true =>
              Redirect(routes.UserController.userProfile()).flashing("msg" -> "profile updated")
            case false =>  Redirect(routes.UserController.userProfile()).flashing("msg" -> "profile not updated")
          }
        }
      })
  }

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

