package etlmail.front.gui.helper

import java.util.concurrent.atomic.AtomicReference

import javax.swing.SwingUtilities

object Edt {
  def invokeAndWait[T](f: => T): T = {
    if (SwingUtilities.isEventDispatchThread()) {
      f
    } else {
      val result = new AtomicReference[T]
      SwingUtilities.invokeAndWait(new Runnable {
        def run() {
          result.set(f)
        }
      })
      result.get
    }
  }
}