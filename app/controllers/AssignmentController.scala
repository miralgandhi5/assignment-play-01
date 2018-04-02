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

class AssignmentController @Inject()(assignmentRepo: AssignmentRepo, assignmentForms: AssignmentForms, cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  def assignment() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.addAssignment(assignmentForms.assignmentForm))
  }

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
          case true => println("Added>>>>>>>>>>>>> " + assignmentData)
            Redirect(routes.AssignmentController.viewAssignments())
          case false => println("Rejected>>>>>>>>>>>>> " + assignmentData)
            Redirect(routes.AssignmentController.assignment())
              .flashing("status" -> "something went wrong")
        }

      }

    )
  }

  def viewAssignments: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    assignmentRepo.getAllAssignments map (assignments => Ok(views.html.viewAssignments(assignments)))
  }

  def deleteAssignment(id: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    assignmentRepo.deleteAssignment(id) map {
      case true => Redirect(routes.AssignmentController.viewAssignments()).flashing("status" -> " assignment deleted")
      case false => Redirect(routes.AssignmentController.viewAssignments()).flashing("status" -> " assignment not deleted")
    }
  }

}
