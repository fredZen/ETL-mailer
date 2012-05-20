package etlmail.engine.css

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.mock.EasyMockSugar
import org.easymock.EasyMockSupport
import org.jsoup.nodes.Document
import org.scalatest.OneInstancePerTest

@RunWith(classOf[JUnitRunner])
class CssInlinerSpec extends EasyMockSugar with FlatSpec with ShouldMatchers with OneInstancePerTest {
  val rule = mock[CssRule]
  val otherRule = mock[CssRule]
  val createRule = mock[(String, String) => CssRule]

  implicit val mocks = MockObjects(rule, otherRule, createRule)

  "extractSimpleRules with one simple rule" should "extract it" in {
    // given
    val inliner = new CssInliner(createRule)
    expecting {
      createRule("far", "yablu").andReturn(rule)
    }

    // when
    whenExecuting {
      val rules = inliner.extractSimpleRules("far{yablu}")
      rules should have size (1)
      rules should contain(rule)
    }
  }

  "extractSimpleRules with one complex rule" should "extract one for each simple rule" in {
    // given
    val inliner = new CssInliner(createRule)
    expecting {
      createRule("far", "totor").andReturn(rule)
      createRule("hmong.crug#ato", "totor").andReturn(otherRule)
    }

    // when
    whenExecuting {
      val rules = inliner.extractSimpleRules("far, hmong.crug#ato{totor}")
      rules should have size (2)
      rules should contain(rule)
      rules should contain(otherRule)
    }
  }

  "extractSimpleRules with two rules" should "extract both" in {
    // given
    val inliner = new CssInliner(createRule)
    expecting {
      createRule("far", "totor").andReturn(rule)
      createRule("hmong.crug#ato", "grung").andReturn(otherRule)
    }

    // when
    whenExecuting {
      val rules = inliner.extractSimpleRules("far{totor} hmong.crug#ato{grung}")
      rules should have size (2)
      rules should contain(rule)
      rules should contain(otherRule)
    }
  }
}
