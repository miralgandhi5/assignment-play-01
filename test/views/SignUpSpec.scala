package views

import models.forms.UserForms
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader}
import play.api.test.Helpers.{contentAsString, _}


class SignUpSpec extends PlaySpec with Mockito {

  "Rending landing / signup page================================" in new App {
    val mockedUserForms = mock[UserForms]
    val messages = mock[MessagesProvider]
    val flash = mock[Flash]
    val requests = mock[RequestHeader]
    val html = views.html.signUp(mockedUserForms.userForm)(messages,flash,requests)
    contentAsString(html) must include("Submit")
  }

}
