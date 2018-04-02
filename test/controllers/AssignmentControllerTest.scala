package controllers

import models.{AssignmentRepo, UserRepo}
import models.forms.{AssignmentForms, UserForms}
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents

class AssignmentControllerTest extends PlaySpec with Mockito {





  def getMockedObject: TestObjects = {

    val mockedAssignmentForms = mock[AssignmentForms]
    val mockedAssignmentRepo = mock[AssignmentRepo]

    val controller = new AssignmentController(mockedAssignmentRepo, mockedAssignmentForms, stubControllerComponents())

    TestObjects(stubControllerComponents(),mockedAssignmentRepo,mockedAssignmentForms,controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents,
                         assignmentRepo: AssignmentRepo,
                         assignmentForms: AssignmentForms,
                         assignmentController: AssignmentController)
}
