package etlmail.front.gui.sendmail

import javax.swing.SwingWorker.StateValue.DONE

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.concurrent.ExecutionException

import javax.swing.SwingWorker

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import etlmail.engine.NewsletterNotification
import etlmail.engine.ToolMailSender
import etlmail.front.gui.helper.UserNotifier
import grizzled.slf4j.Logging

abstract class SendMailAction extends ActionListener with PropertyChangeListener with Logging {

  @Autowired private var notifier: UserNotifier = _

  @Autowired private var toolMailSender: ToolMailSender = _

  @Autowired private var notificationBuilder: NewsletterNotificationBuilder = _

  private var progress: ProgressDialog = _
  private var sendMailWorker: SendMailWorker = _

  def actionPerformed(event: ActionEvent) {
    sendMailWorker = new SendMailWorker(notificationBuilder.build(), toolMailSender)
    sendMailWorker.addPropertyChangeListener(this)
    progress = makeProgressDialog(sendMailWorker)
    progress.setVisible(true)
  }

  /**
   * Only call from EDT
   */
  protected def makeProgressDialog(sendMailWorker: SwingWorker[_, _]): ProgressDialog

  def propertyChange(event: PropertyChangeEvent) {
    if (isDone(event)) {
      progress.setVisible(false)
      progress.dispose()
      progress = null
      try {
        sendMailWorker.get()
      } catch {
        case e: InterruptedException =>
          throw new IllegalStateException("Cannot happen", e)
        case e: ExecutionException =>
          val cause = e.getCause()
          error("Cannot send mail", cause)
          notifier.showError(cause)
      }
    }
  }

  private def isDone(event: PropertyChangeEvent): Boolean =
    "state".equals(event.getPropertyName) && DONE.equals(event.getNewValue)
}

class SendMailWorker(notification: NewsletterNotification, toolMailSender: ToolMailSender) extends SwingWorker[Void, Void] {
  protected def doInBackground(): Void = {
    toolMailSender.sendMail(notification)
    return null
  }
}