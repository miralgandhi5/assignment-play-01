package models.forms

import controllers.UserData
import models.entities.{ForgetPasswordData, LogInData, ProfileData}
import play.api.data.Forms.{mapping, optional, text, _}
import play.api.data._



class UserForms {

  val userForm = Form(
    mapping(
      "firstName" -> text.verifying("Please enter first name", firstName => !firstName.isEmpty),
      "middleName" -> optional(text),
      "lastName" -> text.verifying("Please enter last name", lastName => !lastName.isEmpty),
      "userName" -> text.verifying("Please enter user name", userName => !userName.isEmpty),
      "password" -> nonEmptyText.verifying("Please enter password", pswd => !pswd.isEmpty),
      "confirmPassword" -> nonEmptyText.verifying("Please enter confirm password", pswd => !pswd.isEmpty),
      "mobileNumber" -> text(10, 10),
      "gender" -> nonEmptyText,
      "age" -> number(18, 75),
      "hobbies" -> text.verifying("Select one", hobbies => hobbies.nonEmpty)
    )(UserData.apply)(UserData.unapply).verifying("Password & confirm password do not match",
      fields => fields match {
        case user => user.password == user.confirmPassword
      })
  )

  val loginForm = Form(
    mapping(
      "userName" -> text.verifying("Please enter user name", userName => !userName.isEmpty),
      "password" -> nonEmptyText
    )(LogInData.apply)(LogInData.unapply)
  )

  val forgetPasswordForm = Form(
    mapping(
      "userName" -> text.verifying("Please enter user name", userName => !userName.isEmpty),
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText
    )(ForgetPasswordData.apply)(ForgetPasswordData.unapply).verifying("Password & confirm password do not match",
      fields => fields match {
        case user => user.password == user.confirmPassword
      })
  )

  val profileForm = Form(
    mapping(
      "firstName" -> text.verifying("Please enter first name", firstName => !firstName.isEmpty),
      "middleName" -> optional(text),
      "lastName" -> text.verifying("Please enter last name", lastName => !lastName.isEmpty),
      "password" -> nonEmptyText,
      "mobileNumber" -> text(10, 10),
      "gender" -> nonEmptyText,
      "age" -> number(18, 75),
      "hobbies" -> text.verifying("Select one", hobbies => hobbies.nonEmpty)
    )(ProfileData.apply)(ProfileData.unapply)
  )

}
