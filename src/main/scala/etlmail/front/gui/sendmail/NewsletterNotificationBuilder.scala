package etlmail.front.gui.sendmail

import etlmail.front.gui.helper.ModelUtils.setText

import java.io.File

import javax.swing.text.DefaultStyledDocument
import javax.swing.text.Document

import org.springframework.stereotype.Component

import etlmail.engine.NewsletterNotification
import etlmail.front.gui.choosetemplate.FileDocument
import etlmail.front.gui.choosetemplate.FilenameListener
import etlmail.front.gui.helper.DocumentAdapter
import etlmail.front.gui.helper.Edt._

@Component
class NewsletterNotificationBuilder {
  val subjectDocument = new DefaultStyledDocument
  val templateDocument = new FileDocument
  val fromDocument = new DefaultStyledDocument
  val toDocument = new DefaultStyledDocument
  val ccDocument = new DefaultStyledDocument

  @volatile private var subject_ = ""
  @volatile private var template_ = new File("")
  @volatile private var from_ = ""
  @volatile private var to_ = ""
  @volatile private var cc_ = ""

  def subject: String = subject_
  def template: File = template_
  def from: String = from_
  def to: String = to_
  val cc: String = cc_

  private val variables = Map[String, Any]()

  init()

  private def init() = invokeAndWait {
    subjectDocument.addDocumentListener(new DocumentAdapter {
      protected def update(newText: String) {
        subject_ = newText
      }
    })
    templateDocument.addFilenameListener(new FilenameListener {
      def update(file: File) {
        template_ = file
      }
    })
    fromDocument.addDocumentListener(new DocumentAdapter {
      protected def update(newText: String) {
        from_ = newText
      }
    })
    toDocument.addDocumentListener(new DocumentAdapter {
      protected def update(newText: String) {
        to_ = newText
      }
    })
    ccDocument.addDocumentListener(new DocumentAdapter {
      protected def update(newText: String) {
        cc_ = newText
      }
    })
  }

  def build(): NewsletterNotification = {
    val templateFile = template.getAbsoluteFile
    return new NewsletterNotification(subject, templateFile.getName, templateFile.getParent, from, to, cc, variables)
  }

  /**
   * Only call from the EDT
   */
  def template(template: File): NewsletterNotificationBuilder = {
    templateDocument.setFile(template)
    return this
  }

  /**
   * Only call from the EDT
   */
  def subject(subject: String): NewsletterNotificationBuilder = {
    setText(subjectDocument, subject)
    return this
  }

  /**
   * Only call from the EDT
   */
  def from(from: String): NewsletterNotificationBuilder = {
    setText(fromDocument, from)
    return this
  }

  /**
   * Only call from the EDT
   */
  def to(to: String): NewsletterNotificationBuilder = {
    setText(toDocument, to)
    return this
  }

  /**
   * Only call from the EDT
   */
  def cc(cc: String): NewsletterNotificationBuilder = {
    setText(ccDocument, cc)
    return this
  }
}
