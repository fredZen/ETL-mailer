package etlmail.engine.css
import scala.util.parsing.combinator.JavaTokenParsers
import org.springframework.stereotype.Component

class CssParser extends JavaTokenParsers {
  override protected val whiteSpace = """(?s)/\*.*?\*/""".r // CSS comment

  def parseSheet(sheet: String): List[CssRule] = parseAll(styleSheet, sheet).get

  def styleSheet: Parser[List[CssRule]] = optSpaces ~> rep(rule)

  def rule: Parser[CssRule] = (selectorGroup ~ "{" ~ declarations ~ "}" <~ optSpaces
    ^^ { case selectorGroup ~ "{" ~ declarations ~ "}" => CssRule(selectorGroup, declarations) })

  def wrapWith[T, U <: T](wrapper: Seq[U] => T)(seq: Seq[U]) = seq match {
    case Seq(elt) => elt
    case _ => wrapper(seq)
  }

  def selectorGroup: Parser[SelectorSource] = (repsep(selector, """,\s*""".r)
    ^^ wrapWith(SelectorGrouping))

  def selector: Parser[Selector] = chainl1(simpleSelectorSequence, simpleSelectorSequence, combinator) <~ optSpaces

  def combinator: Parser[(Selector, UncombinedSelector) => Selector] = ("""[ >+~]""".r <~ optSpaces
    ^^ concat)

  def concat(combinator: String): (Selector, UncombinedSelector) => Selector =
    combinator match {
      case " " => Descendant
      case ">" => Child
      case "+" => AdjacentSibling
      case "~" => Sibling
    }

  def simpleSelectorSequence: Parser[UncombinedSelector] = (rep1(typeSelector | classSelector | idSelector)
    ^^ wrapWith(SimpleSelectorSequence))

  def typeSelector: Parser[SimpleSelector] = ("""[a-zA-Z]+""".r
    ^^ TypeSelector)

  def classSelector: Parser[SimpleSelector] = ("""\.[a-zA-Z]+""".r
    ^^ ((_: String).substring(1)).andThen(ClassSelector))

  def idSelector: Parser[SimpleSelector] = ("""#[a-zA-Z]+""".r
    ^^ ((_: String).substring(1)).andThen(IdSelector))

  def declarations: Parser[String] = """[^}]*""".r

  def optSpaces: Parser[Null] = """\s*""".r ^^^ null

}
