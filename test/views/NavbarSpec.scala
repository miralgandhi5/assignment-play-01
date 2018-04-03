package views

import models.forms.UserForms
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader, Session}
import play.api.test.Helpers.{contentAsString, _}

class NavbarSpec extends PlaySpec with Mockito {

  "Rending landing / navbar ================================" in new App {
    val flash = mock[Flash]
    val session = mock[Session]
    when(flash.get("status")) thenReturn None
    val html = views.html.navbar()(flash, session)
    contentAsString(html) must include("Welcome")
  }

}

