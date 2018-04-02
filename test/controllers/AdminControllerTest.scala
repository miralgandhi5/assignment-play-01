package controllers

import akka.util.Timeout
import models.UserRepo
import models.entities.User
import models.forms.UserForms
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, status, stubControllerComponents}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class AdminControllerTest extends PlaySpec with Mockito {
  implicit val duration: Timeout = 20 seconds
  val controller: TestObjects = getMockedObject

  "view users" in {
    val user = User("raj",None,"sharma","raj5","raj5","9999999999","male",24,"reading")
    val userList = List(user)
    when(controller.userRepo.getAllUsers)  thenReturn Future.successful(userList)
    val result = controller.adminController.viewUsers.apply(FakeRequest())
    status(result) must equal(OK)
  }

  "enable user" in {
    when(controller.userRepo.enabledUser("raj5"))  thenReturn Future.successful(true)
    val result = controller.adminController.enableUser("raj5").apply(FakeRequest())
    status(result) must equal(303)
  }

  "disable user" in {
    when(controller.userRepo.enabledUser("raj5"))  thenReturn Future.successful(true)
    val result = controller.adminController.disableUser("raj5").apply(FakeRequest())
    status(result) must equal(303)
  }

  def getMockedObject: TestObjects = {

    val mockedUserRepo = mock[UserRepo]

    val controller = new AdminController(mockedUserRepo, stubControllerComponents())

    TestObjects(stubControllerComponents(),mockedUserRepo, controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents,
                         userRepo: UserRepo,
                         adminController: AdminController)
}
