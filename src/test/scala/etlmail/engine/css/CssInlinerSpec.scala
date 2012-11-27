package etlmail.engine.css

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{OneInstancePerTest, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import org.jsoup.nodes.Document
import org.jsoup.Jsoup

@RunWith(classOf[JUnitRunner])
class CssInlinerSpec extends FlatSpec with ShouldMatchers with OneInstancePerTest {

  "inlineStyle" should "let more specific rules override less specif rules" in {
    val inliner = new CssInliner
    val doc = Jsoup.parse(
      """
        |<html>
        |  <body>
        |    <div>
        |      <a id=moi></a>
        |    </div>
        |  </body>
        |</html>
      """.stripMargin)

    val rules =
      """
        |a {
        |  text-color: red;
        |}
        |div a {
        |  text-color: blue;
        |}
      """.stripMargin

    inliner.inlineStyle(doc, rules)

    doc.getElementById("moi").attr("style") should equal("text-color: red;text-color: blue;")
  }
}
