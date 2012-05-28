package etlmail.engine.css

sealed abstract class Selector extends SelectorSource {
  val specificity: SelectorSpecificity

  def toList: Seq[Selector] = List(this)

  def |(other: UncombinedSelector) = Descendant(this, other)
  def |>(other: UncombinedSelector) = Child(this, other)
  def |+(other: UncombinedSelector) = AdjacentSibling(this, other)
  def |~(other: UncombinedSelector) = Sibling(this, other)
}

abstract class UncombinedSelector extends Selector

abstract class SimpleSelector extends UncombinedSelector
case class TypeSelector(typeName: String) extends SimpleSelector {
  override def toString: String = typeName
  val specificity = SelectorSpecificity(0, 0, 1)
}
case class ClassSelector(className: String) extends SimpleSelector {
  override lazy val toString: String = "." + className
  val specificity = SelectorSpecificity(0, 1, 0)
}
case class IdSelector(id: String) extends SimpleSelector {
  override lazy val toString: String = "#" + id
  val specificity = SelectorSpecificity(1, 0, 0)
}

case class SimpleSelectorSequence(simpleSelectors: Seq[SimpleSelector]) extends UncombinedSelector {
  override lazy val toString: String = simpleSelectors.mkString
  lazy val specificity = simpleSelectors.map(_.specificity).reduce(_ + _)
}

abstract class Combinator(left: Selector, operator: String, right: UncombinedSelector) extends Selector {
  override lazy val toString: String = left.toString + operator + right.toString
  lazy val specificity = left.specificity + right.specificity
}
case class Descendant(left: Selector, right: UncombinedSelector) extends Combinator(left, " ", right)
case class Child(left: Selector, right: UncombinedSelector) extends Combinator(left, ">", right)
case class AdjacentSibling(left: Selector, right: UncombinedSelector) extends Combinator(left, "+", right)
case class Sibling(left: Selector, right: UncombinedSelector) extends Combinator(left, "~", right)

