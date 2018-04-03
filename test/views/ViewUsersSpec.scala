package views

import models.entities.User
import models.forms.UserForms
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.Messages
import play.api.mvc.{Flash, RequestHeader, Session}
import play.api.test.Helpers.{contentAsString, _}


class ViewUserSpec extends PlaySpec with Mockito {

  "Rending landing / view users page================================" in new App {
    val mockedUserForms = mock[UserForms]
    val messages = mock[Messages]
    val flash = mock[Flash]
    val session = mock[Session]
    val users = mock[List[User]]
    val requests = mock[RequestHeader]
    val html = views.html.viewUsers(users)(messages, flash, session, requests)
    contentAsString(html) must include("UserName")
  }

}
