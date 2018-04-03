package views

import models.entities.Assignment
import models.forms.AssignmentForms
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.Messages
import play.api.mvc.{Flash, RequestHeader, Session}
import play.api.test.Helpers.{contentAsString, _}


class ViewAssignmentSpec extends PlaySpec with Mockito {

  "Rending landing / view assignment page================================" in new App {
    val mockedAssignmentForms = mock[AssignmentForms]
    val messages = mock[Messages]
    val flash = mock[Flash]
    val session = mock[Session]
    val assignments = mock[List[Assignment]]
    val requests = mock[RequestHeader]
    val html = views.html.viewAssignments(assignments)(messages, flash, session, requests)
    contentAsString(html) must include("Title")
  }

}
