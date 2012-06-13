package etlmail.front.gui.helper

import javax.swing.text.BadLocationException
import javax.swing.text.Document

object ModelUtils {
  def setText(document: Document, value: String) {
    try {
      document.remove(0, document.getLength)
      document.insertString(0, value, null)
    } catch {
      case e: BadLocationException =>
        throw new IllegalStateException("Cannot happen", e)
    }
  }

  def getText(document: Document): String = {
    try {
      return document.getText(0, document.getLength)
    } catch {
      case e: BadLocationException =>
        throw new IllegalStateException("Cannot happen", e)
    }
  }
}
