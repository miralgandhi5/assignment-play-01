package views

import models.forms.UserForms
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader, Session}
import play.api.test.Helpers.{contentAsString, _}


class UserProfileSpec extends PlaySpec with Mockito {

  "Rending landing / user profile page================================" in new App {
    val mockedUserForms = mock[UserForms]
    val messages = mock[MessagesProvider]
    val flash = mock[Flash]
    val session = mock[Session]
    val requests = mock[RequestHeader]
    val html = views.html.userProfile(mockedUserForms.profileForm)(flash,session,messages,requests)
    contentAsString(html) must include("Update")
  }

}
