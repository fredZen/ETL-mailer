package etlmail.front.gui.application

import org.simplericity.macify.eawt._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MacListener extends ApplicationListener {
  @Autowired private var applicationEventHandler: ApplicationEventHandler = _

  def enable() {
    val app = new DefaultApplication
    app.addPreferencesMenuItem()
    app.setEnabledPreferencesMenu(true)
    app.addAboutMenuItem()
    app.setEnabledAboutMenu(true)
    app.addApplicationListener(this)
  }

  def handleReOpenApplication(e: ApplicationEvent) {
  }

  def handleQuit(e: ApplicationEvent) {
    applicationEventHandler.shutDown(e)
  }

  def handlePrintFile(e: ApplicationEvent) {
  }

  def handlePreferences(e: ApplicationEvent) {
    applicationEventHandler.showPreferences()
  }

  def handleOpenFile(e: ApplicationEvent) {
  }

  def handleOpenApplication(e: ApplicationEvent) {
  }

  def handleAbout(e: ApplicationEvent) {
    e.setHandled(true)
    applicationEventHandler.showAbout()
  }
}