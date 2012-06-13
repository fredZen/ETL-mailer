package etlmail.engine.css

trait SelectorSource {
  def toList: Seq[Selector]
}
