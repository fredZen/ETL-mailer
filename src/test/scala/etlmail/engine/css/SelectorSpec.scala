package etlmail.engine.css

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class SelectorSpec extends FlatSpec with ShouldMatchers {
  "getSpecificity" should "find a html type" in {
    // given
    val selector = TypeSelector("div")

    // then
    selector.specificity should equal(SelectorSpecificity(0, 0, 1))
  }

  "getSpecificity" should "find a class" in {
    // given
    val selector = ClassSelector("toutBeau")

    // then
    selector.specificity should equal(SelectorSpecificity(0, 1, 0))
  }

  "getSpecificity" should "find an id" in {
    // given
    val selector = IdSelector("bidule")

    // then
    selector.specificity should equal(SelectorSpecificity(1, 0, 0))
  }

  "getSpecificity" should "find an id, two classes and one type" in {
    // given
    val selector = SimpleSelectorSequence(List(TypeSelector("div"), IdSelector("bidule"))) |> ClassSelector("toutBeau") | ClassSelector("truc")

    // then
    selector.specificity should equal(SelectorSpecificity(1, 2, 1))
  }
}
