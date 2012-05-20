package etlmail.front.gui.mainframe

import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE

import java.awt.Container
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

import javax.annotation.PostConstruct
import javax.swing._
import javax.swing.text.Document

import net.miginfocom.swing.MigLayout

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import etlmail.front.gui.application.ApplicationEventHandler
import etlmail.front.gui.choosetemplate.ChooseFileAction
import etlmail.front.gui.choosetemplate.FileDocumentChooser
import etlmail.front.gui.helper.FrameHolder
import etlmail.front.gui.preferences.SwingServerConfiguration
import etlmail.front.gui.sendmail.NewsletterNotificationBuilder
import etlmail.front.gui.sendmail.SendMailAction
import grizzled.slf4j.Logging
import etlmail.front.gui.helper.Edt._

@Component
class MainFrame extends FrameHolder with Logging {

  @Autowired
  private var eventHandler: ApplicationEventHandler = _

  @Autowired
  private var serverConfiguration: SwingServerConfiguration = _

  @Autowired
  private var notificationBuilder: NewsletterNotificationBuilder = _

  @Autowired
  private var sendMailAction: SendMailAction = _

  private var fileButton: JButton = _

  private var sendButton: JButton = _

  @PostConstruct
  def init() = invokeAndWait {
    fileButton = new JButton("\u2026")
    sendButton = new JButton("Send")
    makeLayout(frame.getContentPane(), serverConfiguration.passwordDocument)
    addButtonActions()
    frame.setTitle("Mailer GUI")
    frame.setResizable(false)
    frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    frame.addWindowListener(new WindowAdapter {
      override def windowClosing(e: WindowEvent) {
        eventHandler.shutDown(e)
        debug("Shuting down...")
      }
    })
  }

  private def makeLayout(container: Container, password: Document) {
    container.setLayout(new MigLayout( //
      "fill", //
      "[trailing][leading,grow,fill]", //
      ""))

    container.add(new JLabel("Template"))
    container.add(new JTextField(notificationBuilder.templateDocument, null, 20), "split")
    container.add(fileButton, "wrap, grow 0")

    container.add(new JLabel("Subject"))
    container.add(new JTextField(notificationBuilder.subjectDocument, null, 20), "wrap")

    container.add(new JLabel("From"))
    container.add(new JTextField(notificationBuilder.fromDocument, null, 20), "wrap")

    container.add(new JLabel("To"))
    container.add(new JTextField(notificationBuilder.toDocument, null, 20), "wrap")

    container.add(new JLabel("Cc"))
    container.add(new JTextField(notificationBuilder.ccDocument, null, 20), "wrap")

    container.add(new JLabel("Password"))
    container.add(new JPasswordField(password, null, 20), "wrap")

    container.add(sendButton, "span 2, center")
  }

  private def addButtonActions() {
    fileButton.addActionListener(new ChooseFileAction(new FileDocumentChooser(notificationBuilder.templateDocument), frame))
    sendButton.addActionListener(sendMailAction)
    frame.getRootPane.setDefaultButton(sendButton)
  }
}
