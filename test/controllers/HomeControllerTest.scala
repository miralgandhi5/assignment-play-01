package controllers

import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, _}

class HomeControllerTest extends PlaySpec with Mockito {
  val controller = getMockedObject

  "should display index" in {
    val result = controller.homeController.index().apply(FakeRequest().withCSRFToken)
    status(result) must equal(OK)
  }


  "should show error" in {
    val result = controller.homeController.error().apply(FakeRequest().withCSRFToken)
    status(result) must equal(OK)
  }

  def getMockedObject: TestObjects = {


    val controller = new HomeController(stubControllerComponents())

    TestObjects(stubControllerComponents(), controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents, homeController: HomeController)

}
