package controllers

import javax.inject.Inject

import models.AssignmentRepo
import models.entities.Assignment
import models.forms.AssignmentForms
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AssignmentController @Inject()(assignmentRepo: AssignmentRepo, assignmentForms: AssignmentForms,
                                     cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  /**
    * renders assignment form.
    *
    * @return renders addAssignmentForm with status Ok .
    */
  def assignment(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.addAssignment(assignmentForms.assignmentForm))
  }

  /**
    * stores assignment.
    *
    * @return Redirects to viewAssignments with flash showing status and if failed then renders addAssignment with BadRequest.
    */
  def addAssignment(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    assignmentForms.assignmentForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("assignment form has error")
        Future.successful(BadRequest(views.html.addAssignment(formWithErrors)))
      },
      assignmentData => {
        Logger.info("AssignmentData>>>>>>>>>>>>> " + assignmentData)
        val assignment = Assignment(0, assignmentData.title, assignmentData.description)
        assignmentRepo.store(assignment) map {
          case true => Logger.info("Added>>>>>>>>>>>>> " + assignmentData)
            Redirect(routes.AssignmentController.viewAssignments())
          case false => Logger.info("Rejected>>>>>>>>>>>>> " + assignmentData)
            Redirect(routes.AssignmentController.assignment())
              .flashing("status" -> "something went wrong")
        }

      }

    )
  }

  /**
    * view all assignments.
    *
    * @return renders viewAssignment with status Ok.
    */
  def viewAssignments: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    assignmentRepo.getAllAssignments map (assignments => Ok(views.html.viewAssignments(assignments)))
  }

  /**
    * deletes a assignment.
    *
    * @param id id of assignment to be deleted.
    * @return Redirects to viewAssignments with flashing showing status.
    */

  def deleteAssignment(id: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    assignmentRepo.deleteAssignment(id) map {
      case true => Redirect(routes.AssignmentController.viewAssignments()).flashing("status" -> " assignment deleted")
      case false => Redirect(routes.AssignmentController.viewAssignments()).flashing("status" -> " assignment not deleted")
    }
  }

}
