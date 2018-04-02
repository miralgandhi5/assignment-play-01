package models

import javax.inject.Inject

import models.entities.{ProfileData, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.ColumnOption.PrimaryKey
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



  trait UserRepoTrait extends HasDatabaseConfigProvider[JdbcProfile] {

    import profile.api._

    val userQuery: TableQuery[UserProfile] = TableQuery[UserProfile]

    trait UserOps {
      def store(user: User): Future[Boolean]

      def getByName(name: String): Future[User]

      def update(profileData: ProfileData,userName: String): Future[Boolean]

      def getAllUsers: Future[List[User]]

      def enabledUser(userName: String): Future[Boolean]

      def disabledUser(userName: String): Future[Boolean]

      def updatePassword(userName: String, password: String): Future[Boolean]
    }

    class UserProfile(tag: Tag) extends Table[User](tag, "user") {

      def * : ProvenShape[User] = (fname, mname, lname, userName, password, mobileNo, gender, age, hobbies, isEnabled, isAdmin) <> (User.tupled, User.unapply)


      def fname: Rep[String] = column[String]("firstName")

      def mname: Rep[Option[String]] = column[Option[String]]("middleName")

      def lname: Rep[String] = column[String]("lastName")

      def userName: Rep[String] = column[String]("userName",PrimaryKey)

      def password: Rep[String] = column[String]("password")

      def mobileNo: Rep[String] = column[String]("mobileNo")

      def gender: Rep[String] = column[String]("gender")

      def age: Rep[Int] = column[Int]("age")

      def hobbies: Rep[String] = column[String]("hobbies")

      def isEnabled: Rep[Boolean] = column[Boolean]("isEnabled")

      def isAdmin: Rep[Boolean] = column[Boolean]("isAdmin")


    }

  }

  trait UserRepoOps extends UserRepoTrait {

    self: UserRepoTrait =>

    import profile.api._

    def store(user: User): Future[Boolean] = {
     getByName(user.userName) flatMap {
       case Some(_) => Future.successful(false)
       case None => db.run(userQuery += user) map (_ > 0)
     }
    }

    def getByName(name: String): Future[Option[User]] = {
      val query = userQuery.filter(_.userName.toLowerCase === name.toLowerCase).result.headOption
      db.run(query)
    }

   def update(profileData: ProfileData,userName: String): Future[Boolean] = {
     db.run(userQuery.filter(_.userName === userName)
       .map(user => (user.fname,user.mname,user.lname,user.password,user.mobileNo,user.gender,user.age,user.hobbies))
       .update(profileData.firstName,profileData.middleName,profileData.lastName,profileData.password,profileData.mobileNumber,profileData.gender,profileData.age,profileData.hobbies)) map (_ > 0)
   }

    def getAllUsers: Future[List[User]] = {
      db.run(userQuery.to[List].result)
    }

    def enabledUser(userName: String): Future[Boolean] = {
      db.run(userQuery.filter(_.userName === userName).map(user => user.isEnabled).update(true)) map(_ > 0)
      }

    def disabledUser(userName: String): Future[Boolean] = {
      db.run(userQuery.filter(_.userName === userName).map(user => user.isEnabled).update(false)) map(_ > 0)
    }

    def updatePassword(userName: String, password: String): Future[Boolean] = {
      db.run(userQuery.filter(_.userName === userName).map(user => user.password).update(password)) map(_ > 0)
    }
  }

  class UserRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepoOps with UserRepoTrait {

  }



