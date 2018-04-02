package models.forms

import models.entities.AssignmentData
import play.api.data.Form
import play.api.data.Forms.{mapping, _}

class AssignmentForms {

  val assignmentForm = Form(
    mapping(
      "title" -> nonEmptyText.verifying("Please enter title", title => !title.isEmpty),
      "description" -> nonEmptyText.verifying("please enter description", description => !description.isEmpty)
    )(AssignmentData.apply)(AssignmentData.unapply)
  )



}
