package etlmail.front.gui.helper

import javax.swing.JOptionPane.ERROR_MESSAGE
import javax.swing.JOptionPane.showMessageDialog

import javax.swing._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import etlmail.front.gui.about.AboutWindow
import etlmail.front.gui.mainframe.MainFrame

@Component
class UserNotifier {
  import UserNotifier._

  @Autowired private var mainWindow: MainFrame = _
  @Autowired private var aboutWindow: AboutWindow = _

  def showAbout() {
    aboutWindow.show()
  }

  def showError(cause: Throwable) {
    val message = messageArea(cause.getLocalizedMessage)
    showMessageDialog(mainWindow.frame, message, "Somebody set us up the bomb", ERROR_MESSAGE)
  }
}

object UserNotifier {
  def messageArea(message: String): JComponent = {
    val text = new JTextArea(message, 5, 30)
    text.setLineWrap(true)
    text.setWrapStyleWord(true)
    return new JScrollPane(text)
  }
}
