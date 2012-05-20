package etlmail.engine.css
import scala.util.parsing.combinator.JavaTokenParsers
import org.springframework.stereotype.Component

class CssParser extends JavaTokenParsers {
  def parseSheet(sheet: String): List[CssRule] = parseAll(styleSheet, sheet).get

  def styleSheet: Parser[List[CssRule]] = rep(rule)

  def rule: Parser[CssRule] = selector ~ "{" ~ properties ~ "}" ^^
    { case sels ~ "{" ~ props ~ "}" => new CssRule(sels, props) }

  def selector: Parser[List[String]] = repsep(simpleSelector, ",")

  def simpleSelector: Parser[String] = """[^{,]+""".r

  def properties: Parser[String] = """[^}]*""".r
}