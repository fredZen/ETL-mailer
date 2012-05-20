package etlmail.front.gui.application

import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

import etlmail.front.gui.helper.UserNotifier
import etlmail.front.gui.preferences.PreferencesWindow

@Component
class ApplicationEventHandler {
  @Autowired private var eventPublisher: ApplicationEventPublisher = _
  @Autowired private var notifier: UserNotifier = _
  @Autowired private var preferencesWindowProvider: ObjectFactory[PreferencesWindow] = _

  def shutDown(cause: Any) {
    eventPublisher.publishEvent(new ShutdownEvent(cause))
  }

  /**
   * Only call from EDT
   */
  def showPreferences() {
    preferencesWindowProvider.getObject.show()
  }

  def showAbout() {
    notifier.showAbout()
  }
}
