package etlmail.engine.css
import scala.util.parsing.combinator.JavaTokenParsers
import org.springframework.stereotype.Component

class CssParser extends JavaTokenParsers {
  override protected val whiteSpace = """(?s)/\*.*?\*/""".r // CSS comment

  def parseSheet(sheet: String): List[CssRule] = parseAll(styleSheet, sheet).get

  def repNM[T](n1: Int, n2: Int, p: Parser[T]): Parser[List[T]] =
    repN(n1, p) ~ repN(n2-n1, p?) ^^ {case l1 ~ l2 => l1 ++ (l2.flatten)}

  def flatten(o: Any): String = o match {
    case s: String => s
    case ss: List[String] => ss.mkString("")
    case s1 ~ s2 => flatten(s1) + flatten(s2)
  }

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

  def nonascii: Parser[String] = "[\240-\377]".r

  def h: Parser[String] = """[0-9a-f]""".r

  def unicode: Parser[String] = """\\""".r ~ repNM(1, 6, h) ~ "(\r\n|[ \t\r\n\f])?".r ^^ flatten

  def escape: Parser[String] = unicode | "\\[^\r\n\f0-9a-f]"

  def nmstart: Parser[String] = "[_a-z]".r | nonascii | escape

  def nmchar: Parser[String] = "[_a-z0-9-]".r | nonascii | escape

  def identifier: Parser[String] = """-?""".r ~ nmstart ~ (nmchar *) ^^ flatten

  def typeSelector: Parser[SimpleSelector] = identifier ^^ TypeSelector

  def classSelector: Parser[SimpleSelector] = """\.""".r ~> identifier ^^ ClassSelector

  def idSelector: Parser[SimpleSelector] = """#""".r ~> identifier ^^ IdSelector

  def declarations: Parser[String] = """[^}]*""".r

  def optSpaces: Parser[Null] = """\s*""".r ^^^ null

}
