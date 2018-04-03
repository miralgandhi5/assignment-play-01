package controllers

import models.AssignmentRepo
import models.entities.Assignment
import models.forms.AssignmentForms
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, _}

import scala.concurrent.Future

class AssignmentControllerTest extends PlaySpec with Mockito {

  val controller: TestObjects = getMockedObject

  "should display add assignment" in {
    val assignmentForm = new AssignmentForms {}.assignmentForm
    when(controller.assignmentForms.assignmentForm) thenReturn assignmentForm
    val result = controller.assignmentController.assignment().apply(FakeRequest().withCSRFToken)
    status(result) must equal(OK)
  }

  "display assignments" in {
    val assignment = Assignment(1, "play", "main assignment")
    val assignmentList = List(assignment)
    when(controller.assignmentRepo.getAllAssignments) thenReturn Future.successful(assignmentList)
    val result = controller.assignmentController.viewAssignments.apply(FakeRequest().withCSRFToken)
    status(result) must equal(OK)
  }

  "delete assignment" in {
    when(controller.assignmentRepo.deleteAssignment(1)) thenReturn Future.successful(true)
    val result = controller.assignmentController.deleteAssignment(1).apply(FakeRequest().withCSRFToken)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/viewAssignments")
  }

  " does not delete assignment" in {
    when(controller.assignmentRepo.deleteAssignment(1)) thenReturn Future.successful(false)
    val result = controller.assignmentController.deleteAssignment(1).apply(FakeRequest().withCSRFToken)
    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/viewAssignments")
  }

  "add assignment" in {
    val assignmentForm = new AssignmentForms {}.assignmentForm
    val assignment = Assignment(0, "play", "main assignment")
    when(controller.assignmentForms.assignmentForm) thenReturn assignmentForm
    when(controller.assignmentRepo.store(assignment)) thenReturn Future.successful(true)


    val request = FakeRequest(POST, "/addAssignment ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "title" -> "play", "description" -> "main assignment")
      .withCSRFToken

    val result = controller.assignmentController.addAssignment().apply(request)

    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/viewAssignments")
  }


  "does not add assignment" in {
    val assignmentForm = new AssignmentForms {}.assignmentForm
    val assignment = Assignment(0, "play", "main assignment")
    when(controller.assignmentForms.assignmentForm) thenReturn assignmentForm
    when(controller.assignmentRepo.store(assignment)) thenReturn Future.successful(false)


    val request = FakeRequest(POST, "/addAssignment ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "title" -> "play", "description" -> "main assignment")
      .withCSRFToken

    val result = controller.assignmentController.addAssignment().apply(request)

    status(result) must equal(303)
    redirectLocation(result) mustBe Some("/assignment")
  }


  "does not add assignment as form has errors" in {
    val assignmentForm = new AssignmentForms {}.assignmentForm
    val assignment = Assignment(0, "play", "main assignment")
    when(controller.assignmentForms.assignmentForm) thenReturn assignmentForm
    when(controller.assignmentRepo.store(assignment)) thenReturn Future.successful(false)


    val request = FakeRequest(POST, "/addAssignment ").withFormUrlEncodedBody("csrfToken"
      -> "9c48f08", "title" -> "", "description" -> "main assignment")
      .withCSRFToken

    val result = controller.assignmentController.addAssignment().apply(request)

    status(result) must equal(400)
  }

  def getMockedObject: TestObjects = {

    val mockedAssignmentForms = mock[AssignmentForms]
    val mockedAssignmentRepo = mock[AssignmentRepo]

    val controller = new AssignmentController(mockedAssignmentRepo, mockedAssignmentForms, stubControllerComponents())

    TestObjects(stubControllerComponents(), mockedAssignmentRepo, mockedAssignmentForms, controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents,
                         assignmentRepo: AssignmentRepo,
                         assignmentForms: AssignmentForms,
                         assignmentController: AssignmentController)

}
