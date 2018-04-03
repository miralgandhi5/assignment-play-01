package views

import models.forms.UserForms
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader}
import play.api.test.Helpers.{contentAsString, _}

class SignInSpec extends PlaySpec with Mockito {

  "Rending landing / signIn page================================" in new App {
    val mockedUserForms = mock[UserForms]
    val messages = mock[MessagesProvider]
    val flash = mock[Flash]
    val requests = mock[RequestHeader]
    when(flash.get("login")) thenReturn None
    val html = views.html.signin(mockedUserForms.loginForm)(flash,messages,requests)
    contentAsString(html) must include("LogIn")
  }

}
