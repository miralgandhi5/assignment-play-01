package models.entities

case class User(firstName: String, middleName: Option[String], lastName: String, userName: String, password: String, mobile: String, gender: String, age: Int, hobbies: String,isEnabled: Boolean = true,isAdmin: Boolean = false)

case class LogInData(userName: String, password:String)

case class ForgetPasswordData(userName: String, password:String, confirmPassword: String)

case class ProfileData(firstName: String, middleName: Option[String], lastName: String, password: String,mobileNumber: String, gender: String, age: Int, hobbies: String)