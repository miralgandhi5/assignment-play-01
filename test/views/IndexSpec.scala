package views

import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.test.Helpers.{contentAsString, _}

class IndexSpec extends PlaySpec with Mockito {

  "Rending landing / index page================================" in new App {
    val html = views.html.index()
    contentAsString(html) must include("signIn")
  }

}