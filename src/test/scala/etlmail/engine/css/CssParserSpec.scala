package etlmail.engine.css

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.jsoup.nodes.Document
import org.scalatest.OneInstancePerTest

@RunWith(classOf[JUnitRunner])
class CssParserSpec extends FlatSpec with ShouldMatchers with OneInstancePerTest {
  val parser = new CssParser()
  val xyzSelector = SimpleSelectorSequence(List(TypeSelector("x"), ClassSelector("y"), IdSelector("z")))
  val abcSelector = SimpleSelectorSequence(List(TypeSelector("ab"), IdSelector("c")))

  "extractSimpleRules with one simple rule" should "extract it" in {
    // when
    val rules = parser.parseSheet("x.y#z{yadda:badda}")

    // then
    rules should have size (1)
    rules should contain(CssRule(xyzSelector, "yadda:badda"))
  }

  "extractSimpleRules with a comment before the rule" should "skip it" in {
    // when
    val rules = parser.parseSheet("/*yes*/x.y#z{yadda:badda}")

    // then
    rules should have size (1)
    rules should contain(CssRule(xyzSelector, "yadda:badda"))
  }

  "extractSimpleRules with a comment after the rule" should "skip it" in {
    // when
    val rules = parser.parseSheet("x.y#z/*yes*/{yadda:badda}")

    // then
    rules should have size (1)
    rules should contain(CssRule(xyzSelector, "yadda:badda"))
  }

  "multiple comments" should "preserve the code between the two comments" in {
    // when
    val rules = parser.parseSheet("/*no*/x.y#z/*yes*/{yadda:badda}")

    // then
    rules should have size (1)
    rules should contain(CssRule(xyzSelector, "yadda:badda"))
  }

  "multiline comments" should "be handled like other comments" in {
    // when
    val rules = parser.parseSheet("/*no\r*/x.y#z/*yes\n*/{yadda:badda}")

    // then
    rules should have size (1)
    rules should contain(CssRule(xyzSelector, "yadda:badda"))
  }

  "spaces" should "be ignored before the stylesheet" in {
    // when
    val rules = parser.parseSheet("   x.y#z{yadda:badda}")

    // then
    rules should have size (1)
    rules should contain(CssRule(xyzSelector, "yadda:badda"))
  }

  "spaces" should "be ignored after the stylesheet" in {
    // when
    val rules = parser.parseSheet("x.y#z{yadda:badda}   ")

    // then
    rules should have size (1)
    rules should contain(CssRule(xyzSelector, "yadda:badda"))
  }

  "spaces" should "be ignored between the rule and the declarations" in {
    // when
    val rules = parser.parseSheet("x.y#z   {yadda:badda}")

    // then
    rules should have size (1)
    rules should contain(CssRule(xyzSelector, "yadda:badda"))
  }

  "contextual selectors" should "not be split or squashed" in {
    // when
    val rules = parser.parseSheet("x.y#z ab#c{yadda:badda}")

    // then
    rules should have size (1)
    rules should contain(CssRule(Descendant(xyzSelector, abcSelector), "yadda:badda"))
  }

  "a group of selectors" should "result in multiple rules" in {
    // when
    val rules = parser.parseSheet("x.y#z , ab#c{yadda:badda}")

    // then
    rules should have size (1)
    rules should contain(CssRule(SelectorGrouping(List(xyzSelector, abcSelector)), "yadda:badda"))
  }

  "a group of letters" should "be a valid identifier" in {
    val parsed = parser.parseAll(parser.identifier, "azerty").get
    parsed should be ("azerty")
  }

  "h1" should "be a valid type selector" in {
    val parsed = parser.parseAll(parser.typeSelector, "h1").get
    parsed should be (TypeSelector("h1"))
  }

  ".pipo" should "be a valid class selector" in {
    val parsed = parser.parseAll(parser.classSelector, ".pipo").get
    parsed should be (ClassSelector("pipo"))
  }

  ".molo" should "be a valid id selector" in {
    val parsed = parser.parseAll(parser.idSelector, "#molo").get
    parsed should be (IdSelector("molo"))
  }
}
