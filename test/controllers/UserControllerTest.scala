import controllers.UserController
import models.UserRepo
import models.entities.{LogInData, ProfileData, User}
import models.forms.UserForms
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, _}

import scala.concurrent.Future

class UserControllerTest extends PlaySpec with Mockito {
  val controller: TestObjects = getMockedObject

  "should store a user" in {
    val profileData = ProfileData("raj", None, "sharma", "raj5", "9999999999", "male", 24, "reading")
    val userForm = new UserForms {}.userForm
    val profileForm = new UserForms {}.profileForm.fill(profileData)
    val payload = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    when(controller.userForm.userForm) thenReturn userForm
    when(controller.userForm.profileForm) thenReturn profileForm
    when(controller.userRepo.store(payload)) thenReturn Future.successful(true)
    val request = FakeRequest(POST, "/submitData ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "firstName" -> "raj", "lastName" -> "sharma", "userName" -> "raj5", "password" -> "raj5", "confirmPassword" -> "raj5", "mobileNumber" -> "9999999999", "gender" -> "male", "age" -> "24", "hobbies" -> "reading")
      .withCSRFToken
    val result = controller.userController.submitData().apply(request)
    status(result) must equal(OK)
  }

  "should not store a user" in {
    val profileData = ProfileData("raj", None, "sharma", "raj5", "9999999999", "male", 24, "reading")
    val userForm = new UserForms {}.userForm
    val profileForm = new UserForms {}.profileForm.fill(profileData)
    val payload = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    when(controller.userForm.userForm) thenReturn userForm
    when(controller.userForm.profileForm) thenReturn profileForm
    when(controller.userRepo.store(payload)) thenReturn Future.successful(false)
    val request = FakeRequest(POST, "/submitData ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "firstName" -> "raj", "lastName" -> "sharma", "userName" -> "raj5", "password" -> "raj5", "confirmPassword" -> "raj5", "mobileNumber" -> "9999999999", "gender" -> "male", "age" -> "24", "hobbies" -> "reading")
      .withCSRFToken
    val result = controller.userController.submitData().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/index")

  }

  "should not store a user as form has error" in {
    val profileData = ProfileData("raj", None, "sharma", "raj5", "9999999999", "male", 24, "reading")
    val userForm = new UserForms {}.userForm
    val profileForm = new UserForms {}.profileForm.fill(profileData)
    val payload = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    when(controller.userForm.userForm) thenReturn userForm
    when(controller.userForm.profileForm) thenReturn profileForm
    val request = FakeRequest(POST, "/submitData ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "firstName" -> "raj", "lastName" -> "", "userName" -> "raj5", "password" -> "raj15", "confirmPassword" -> "raj5", "mobileNumber" -> "9999999999", "gender" -> "male", "age" -> "24", "hobbies" -> "reading")
      .withCSRFToken
    val result = controller.userController.submitData().apply(request)
    status(result) must equal(400)

  }

  "should display signup" in {
    val result = controller.userController.signUp().apply(FakeRequest().withCSRFToken)
    status(result) must equal(OK)
  }


  "should display signIn" in {
    val loginData = LogInData("raj5", "raj5")
    val loginForm = new UserForms {}.loginForm.fill(loginData)
    when(controller.userForm.loginForm) thenReturn loginForm
    val result = controller.userController.signIn().apply(FakeRequest().withCSRFToken)
    status(result) must equal(OK)
  }

  "should login" in {
    val loginData = LogInData("raj5", "raj5")
    val user = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    val loginForm = new UserForms {}.loginForm.fill(loginData)
    when(controller.userForm.loginForm) thenReturn loginForm
    when(controller.userRepo.getByName(loginData.userName)) thenReturn Future.successful(Some(user))
    val request = FakeRequest(POST, "/login ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "userName" -> "raj5", "password" -> "raj5")
      .withCSRFToken
    val result = controller.userController.login().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/userProfile")
  }

  "should not login as invalid user" in {
    val loginData = LogInData("raj5", "raj5")
    val loginForm = new UserForms {}.loginForm.fill(loginData)
    when(controller.userForm.loginForm) thenReturn loginForm
    when(controller.userRepo.getByName(loginData.userName)) thenReturn Future.successful(None)
    val request = FakeRequest(POST, "/login ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "userName" -> "raj5", "password" -> "raj5")
      .withCSRFToken
    val result = controller.userController.login().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/signIn")
  }

  "should not login as invalid password" in {
    val loginData = LogInData("raj5", "raj5")
    val user = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    val loginForm = new UserForms {}.loginForm.fill(loginData)
    when(controller.userForm.loginForm) thenReturn loginForm
    when(controller.userRepo.getByName(loginData.userName)) thenReturn Future.successful(Some(user))
    val request = FakeRequest(POST, "/login ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "userName" -> "raj5", "password" -> "raj")
      .withCSRFToken
    val result = controller.userController.login().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/signIn")
  }

  "should not login as invalid form" in {
    val loginData = LogInData("raj5", "raj5")
    val loginForm = new UserForms {}.loginForm.fill(loginData)
    when(controller.userForm.loginForm) thenReturn loginForm
    when(controller.userRepo.getByName(loginData.userName)) thenReturn Future.successful(None)
    val request = FakeRequest(POST, "/login ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "userName" -> "raj5", "password" -> "")
      .withCSRFToken
    val result = controller.userController.login().apply(request)
    status(result) must equal(400)
  }

  "should display forgetPassword" in {
    val forgetPasswordForm = new UserForms {}.forgetPasswordForm
    when(controller.userForm.forgetPasswordForm) thenReturn forgetPasswordForm
    val result = controller.userController.forgetPassword().apply(FakeRequest().withCSRFToken)
    status(result) must equal(OK)
  }

  "should logout" in {
    val result = controller.userController.logout().apply(FakeRequest().withCSRFToken)
    status(result) must equal(OK)
  }

  "should show userProfile" in {
    val user = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    val profileData = ProfileData("raj", None, "sharma", "raj5", "9999999999", "male", 24, "reading")
    val profileForm = new UserForms {}.profileForm.fill(profileData)
    when(controller.userForm.profileForm) thenReturn profileForm
    when(controller.userRepo.getByName("raj5")) thenReturn Future.successful(Some(user))
    val result = controller.userController.userProfile.apply(FakeRequest().withSession("name" -> "raj5").withCSRFToken)
    status(result) must equal(OK)
  }

  "should not show userProfile if user not present" in {
    val user = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    val profileData = ProfileData("raj", None, "sharma", "raj5", "9999999999", "male", 24, "reading")
    val profileForm = new UserForms {}.profileForm.fill(profileData)
    when(controller.userForm.profileForm) thenReturn profileForm
    when(controller.userRepo.getByName("raj5")) thenReturn Future.successful(None)
    val result = controller.userController.userProfile.apply(FakeRequest().withSession("name" -> "raj5").withCSRFToken)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/error")

  }


  "should not show userProfile as not in session" in {
    val user = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    val profileData = ProfileData("raj", None, "sharma", "raj5", "9999999999", "male", 24, "reading")
    val profileForm = new UserForms {}.profileForm.fill(profileData)
    when(controller.userForm.profileForm) thenReturn profileForm
    when(controller.userRepo.getByName("raj5")) thenReturn Future.successful(None)
    val result = controller.userController.userProfile.apply(FakeRequest().withSession().withCSRFToken)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/error")
  }

  "update user" in {
    val user = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    val profileData = ProfileData("raj", None, "sharma", "raj5", "9999999999", "male", 24, "reading")
    val profileForm = new UserForms {}.profileForm
    when(controller.userForm.profileForm) thenReturn profileForm
    when(controller.userRepo.getByName("raj5")) thenReturn Future.successful(Some(user))
    when(controller.userRepo.update(profileData, "raj5")) thenReturn Future.successful(true)
    val request = FakeRequest(POST, "/updateUser ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "firstName" -> "raj", "lastName" -> "sharma", "password" -> "raj5", "mobileNumber" -> "9999999999", "gender" -> "male", "age" -> "24", "hobbies" -> "reading").withSession("name" -> "raj5")
      .withCSRFToken
    val result = controller.userController.updateUser().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/userProfile")
  }

  "does not update user as user not present" in {
    val user = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    val profileData = ProfileData("raj", None, "sharma", "raj5", "9999999999", "male", 24, "reading")
    val profileForm = new UserForms {}.profileForm
    when(controller.userForm.profileForm) thenReturn profileForm
    when(controller.userRepo.getByName("raj5")) thenReturn Future.successful(None)
    val request = FakeRequest(POST, "/updateUser ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "firstName" -> "raj", "lastName" -> "sharma", "password" -> "raj5", "mobileNumber" -> "9999999999", "gender" -> "male", "age" -> "24", "hobbies" -> "reading").withSession("name" -> "raj5")
      .withCSRFToken
    val result = controller.userController.updateUser().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/userProfile")
  }

  "does not update user" in {
    val user = User("raj", None, "sharma", "raj5", "raj5", "9999999999", "male", 24, "reading")
    val profileData = ProfileData("raj", None, "sharma", "raj5", "9999999999", "male", 24, "reading")
    val profileForm = new UserForms {}.profileForm
    when(controller.userForm.profileForm) thenReturn profileForm
    when(controller.userRepo.getByName("raj5")) thenReturn Future.successful(None)
    when(controller.userRepo.update(profileData, "raj5")) thenReturn Future.successful(false)
    val request = FakeRequest(POST, "/updateUser ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "firstName" -> "raj", "lastName" -> "sharma", "password" -> "raj5", "mobileNumber" -> "9999999999", "gender" -> "male", "age" -> "24", "hobbies" -> "reading").withSession("name" -> "raj5")
      .withCSRFToken
    val result = controller.userController.updateUser().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/userProfile")
  }

  "does not update user as form invalid" in {

    val profileForm = new UserForms {}.profileForm
    when(controller.userForm.profileForm) thenReturn profileForm
    val request = FakeRequest(POST, "/updateUser ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "firstName" -> "raj", "lastName" -> "", "password" -> "raj5", "mobileNumber" -> "9999999999", "gender" -> "male", "age" -> "24", "hobbies" -> "reading").withSession("name" -> "raj5")
      .withCSRFToken
    val result = controller.userController.updateUser().apply(request)
    status(result) must equal(400)
  }

  "does not update user as not in session" in {
    val profileForm = new UserForms {}.profileForm
    when(controller.userForm.profileForm) thenReturn profileForm
    val request = FakeRequest(POST, "/updateUser ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "firstName" -> "raj", "lastName" -> "raj5", "password" -> "raj5", "mobileNumber" -> "9999999999", "gender" -> "male", "age" -> "24", "hobbies" -> "reading")
      .withCSRFToken
    val result = controller.userController.updateUser().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/error")
  }

  "update password" in {
    val forgetPasswordForm = new UserForms {}.forgetPasswordForm
    when(controller.userForm.forgetPasswordForm) thenReturn forgetPasswordForm
    when(controller.userRepo.updatePassword("raj5", "raj5")) thenReturn Future.successful(true)
    val request = FakeRequest(POST, "/updateUser ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "userName" -> "raj5", "password" -> "raj5", "confirmPassword" -> "raj5").withCSRFToken
    val result = controller.userController.updatePassword().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/forgetPassword")
  }

  "not update password" in {
    val forgetPasswordForm = new UserForms {}.forgetPasswordForm
    when(controller.userForm.forgetPasswordForm) thenReturn forgetPasswordForm
    when(controller.userRepo.updatePassword("raj5", "raj5")) thenReturn Future.successful(false)
    val request = FakeRequest(POST, "/updateUser ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "userName" -> "raj5", "password" -> "raj5", "confirmPassword" -> "raj5").withCSRFToken
    val result = controller.userController.updatePassword().apply(request)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/forgetPassword")
  }

  "not update password as invalid form" in {
    val forgetPasswordForm = new UserForms {}.forgetPasswordForm
    when(controller.userForm.forgetPasswordForm) thenReturn forgetPasswordForm
    val request = FakeRequest(POST, "/updateUser ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "userName" -> "raj5", "password" -> "j5", "confirmPassword" -> "raj5").withCSRFToken
    val result = controller.userController.updatePassword().apply(request)
    status(result) must equal(400)
  }


  def getMockedObject: TestObjects = {
    val mockedUserForms = mock[UserForms]
    val mockedUserRepo = mock[UserRepo]

    val controller = new UserController(mockedUserRepo, mockedUserForms, stubControllerComponents())

    TestObjects(stubControllerComponents(), mockedUserForms, mockedUserRepo, controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents,
                         userForm: UserForms,
                         userRepo: UserRepo,
                         userController: UserController)

}