package etlmail.front.gui.application

import java.awt.Window
import scala.collection.mutable.{ WeakHashMap, SynchronizedMap }

import org.apache.commons.lang.Validate
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import scala.collection.JavaConversions._

@Component
class WindowJanitor extends ApplicationListener[ShutdownEvent] {
  private val windows = new WeakHashMap[Window, WindowJanitor] with SynchronizedMap[Window, WindowJanitor]

  def onApplicationEvent(event: ShutdownEvent) {
    windows.synchronized {
      for (window <- windows.keySet) {
        window.dispose()
      }
    }
  }

  def register(window: Window) {
    Validate.notNull(window)
    windows.put(window, this)
  }
}
