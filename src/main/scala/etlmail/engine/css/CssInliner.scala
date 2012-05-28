package etlmail.engine.css

import org.springframework.beans.factory.annotation.Autowired
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._
import scala.collection.LinearSeq

@Component
class CssInliner {
  private val parser = new CssParser

  def inlineStyles(doc: Document) {
    for (style <- doc.select("style")) {
      val styleRules = style.getAllElements().get(0).data.replaceAll("\n", "").trim
      inlineStyle(doc, styleRules)
      style.remove()
    }
  }

  def inlineStyle(doc: Document, styleRules: String) {
    val cssRules = extractSimpleRules(styleRules)
    for (rule <- cssRules.sorted(Ordering[SimpleCssRule].reverse)) {
      rule.prependProperties(doc)
    }
  }

  def extractSimpleRules(styleRules: String): List[SimpleCssRule] =
    (for {
      CssRule(selectors, properties) <- parser.parseSheet(styleRules)
      selector <- selectors.toList
    } yield SimpleCssRule(selector, properties)).toList
}
