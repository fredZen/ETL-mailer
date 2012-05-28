package etlmail.engine.css

case class SelectorSpecificity(ids: Int, classes: Int, types: Int) extends Ordered[SelectorSpecificity] {

  def +(other: SelectorSpecificity): SelectorSpecificity = SelectorSpecificity(ids + other.ids, classes + other.classes, types + other.types)

  override def compare(o: SelectorSpecificity): Int = {
    var result = ids - o.ids
    if (result == 0) {
      result = classes - o.classes
    }
    if (result == 0) {
      result = types - o.types
    }
    return result
  }

  override def hashCode(): Int = {
    val prime = 31
    var result = 1
    result = (prime * result) + classes
    result = (prime * result) + ids
    result = (prime * result) + types
    return result
  }

  override def equals(obj: Any): Boolean =
    (this eq obj.asInstanceOf[AnyRef]) || (obj match {
      case null => false
      case other: SelectorSpecificity =>
        classes == other.classes && ids == other.ids && types == other.types
      case _ => false
    })

  override def toString(): String = "SelectorSpecificit(" + ids + ", " + classes + ", " + types + ")"
}
