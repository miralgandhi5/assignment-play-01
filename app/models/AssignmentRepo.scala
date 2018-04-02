package models

import javax.inject.Inject

import models.entities.Assignment
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AssignmentRepoTrait extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val assignmentQuery: TableQuery[AssignmentProfile] = TableQuery[AssignmentProfile]

  trait AssignmentOps {
    def store(assignmentData: Assignment): Future[Boolean]

    def getAllAssignments: Future[List[Assignment]]

    def deleteAssignment(id: Int): Future[Boolean]

  }

  class AssignmentProfile(tag: Tag) extends Table[Assignment](tag, "assignment") {

    def * : ProvenShape[Assignment] = (id, title, description) <> (Assignment.tupled, Assignment.unapply)

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")


  }


}

trait AssignmentRepoOps extends AssignmentRepoTrait {

  self: AssignmentRepoTrait =>

  import profile.api._

  def store(assignment: Assignment): Future[Boolean] = {
    db.run(assignmentQuery += assignment) map (_ > 0)
  }

  def getAllAssignments: Future[List[Assignment]] = {
    db.run(assignmentQuery.to[List].result)
  }

  def deleteAssignment(id: Int): Future[Boolean] = {
    db.run(assignmentQuery.filter(_.id === id).delete).map(_ > 0)
  }
}

class AssignmentRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AssignmentRepoOps with AssignmentRepoTrait {

}
