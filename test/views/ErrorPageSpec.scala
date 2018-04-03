package views

import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.test.Helpers.{contentAsString, _}

class ErrorPageSpec extends PlaySpec with Mockito {

  "Rending landing / error page================================" in new App {
    val html = views.html.errorPage()
    contentAsString(html) must include("ACCESS")
  }

}
