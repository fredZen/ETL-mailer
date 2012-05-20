package etlmail.engine.css

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._
import scala.collection.LinearSeq

@Component
class CssInliner(makeCssRule: (String, String) => CssRule) {
  def this() = this((selector: String, properties: String) => new CssRule(selector, properties))

  def inlineStyles(doc: Document) {
    for (style <- doc.select("style")) {
      val styleRules = style.getAllElements().get(0).data.replaceAll("\n", "").trim
      inlineStyle(doc, styleRules)
      style.remove()
    }
  }

  def inlineStyle(doc: Document, styleRules: String) {
    val cssRules = extractSimpleRules(styleRules)
    for (rule <- cssRules.sorted(Ordering[CssRule].reverse)) {
      rule.prependProperties(doc)
    }
  }

  def extractSimpleRules(styleRules: String): List[CssRule] =
    (for {
      Array(selector, properties) <- styleRules.split("[{}]").grouped(2)
      simpleSelector <- selector.split(",")
    } yield makeCssRule(simpleSelector.trim, properties)).toList
}
