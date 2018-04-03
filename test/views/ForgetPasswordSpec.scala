package views

import models.forms.UserForms
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader, Session}
import play.api.test.Helpers.{contentAsString, _}

class ForgetPasswordSpec extends PlaySpec with Mockito {

  "Rending landing / forget password page================================" in new App {
    val mockedUserForms = mock[UserForms]
    val messages = mock[MessagesProvider]
    val flash = mock[Flash]
    val session = mock[Session]
    val requests = mock[RequestHeader]
    when(flash.get("status")) thenReturn None
    val html = views.html.forgetPassword(mockedUserForms.forgetPasswordForm)(flash, session, messages, requests)
    contentAsString(html) must include("Forget Password")
  }

}
