package views

import models.forms.{AssignmentForms, UserForms}
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader, Session}
import play.api.test.Helpers.{contentAsString, _}

class AddAssignmentSpec extends PlaySpec with Mockito {

  "Rending landing / addAssignment page================================" in new App {
    val mockedAssignmentForms = mock[AssignmentForms]
    val messages = mock[MessagesProvider]
    val flash = mock[Flash]
    val session = mock[Session]
    val requests = mock[RequestHeader]
    when(flash.get("status")) thenReturn None
    val html = views.html.addAssignment(mockedAssignmentForms.assignmentForm)(flash,session,messages,requests)
    contentAsString(html) must include("Title")
  }

}
