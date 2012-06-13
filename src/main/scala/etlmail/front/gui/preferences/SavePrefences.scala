package etlmail.front.gui.preferences

import java.io._
import java.util.Properties

import javax.annotation.PostConstruct

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

import com.google.common.io.Closeables

import grizzled.slf4j.Logging

import etlmail.front.gui.application.ShutdownEvent
import etlmail.front.gui.sendmail.NewsletterNotificationBuilder
import etlmail.front.gui.helper.Edt._

object SavePrefences {
  private val FILENAME: String = "mailgui.properties"

  private val TO: String = "to"
  private val FROM: String = "from"
  private val CC: String = "cc"
  private val SUBJECT: String = "subject"
  private val TEMPLATE: String = "template"

  private val SERVER: String = "server"
  private val PORT: String = "port"
  private val USER: String = "user"
}

@Component
class SavePrefences extends ApplicationListener[ShutdownEvent] with Logging {
  import SavePrefences._

  @Autowired private var notificationBuilder: NewsletterNotificationBuilder = _
  @Autowired private var serverConfiguration: SwingServerConfiguration = _

  @PostConstruct
  def init() {
    fromProperties(readProperties())
  }

  private def readProperties(): Properties = {
    val restored = new Properties
    var in: InputStream = null
    try {
      in = new BufferedInputStream(new FileInputStream(FILENAME))
      restored.load(in)
      in.close()
    } catch {
      case e: IOException =>
        error("Cannot read preferences", e)
    } finally {
      Closeables.closeQuietly(in)
    }
    return restored
  }

  private def fromProperties(restored: Properties) = invokeAndWait {
    notificationBuilder.to(restored.getProperty(TO))
    notificationBuilder.cc(restored.getProperty(CC))
    notificationBuilder.from(restored.getProperty(FROM))
    notificationBuilder.subject(restored.getProperty(SUBJECT))
    val template = restored.getProperty(TEMPLATE)
    if (template != null) {
      notificationBuilder.template(new File(template))
    }

    serverConfiguration.setHost(restored.getProperty(SERVER))
    val port = restored.getProperty(PORT)
    if (null != port) {
      serverConfiguration.setPort(Integer.parseInt(port))
    }
    serverConfiguration.setUsername(restored.getProperty(USER))
  }

  @Override
  def onApplicationEvent(event: ShutdownEvent) {
    writeProperties(toProperties())
  }

  private def toProperties(): Properties = invokeAndWait {
    val toSave = new Properties
    toSave.setProperty(TO, notificationBuilder.to)
    toSave.setProperty(CC, notificationBuilder.cc)
    toSave.setProperty(FROM, notificationBuilder.from)
    toSave.setProperty(SUBJECT, notificationBuilder.subject)
    toSave.setProperty(TEMPLATE, notificationBuilder.template.getPath)

    toSave.setProperty(SERVER, serverConfiguration.host)
    toSave.setProperty(PORT, Integer.toString(serverConfiguration.port))
    toSave.setProperty(USER, serverConfiguration.username)

    toSave
  }

  private def writeProperties(toSave: Properties) {
    var out: OutputStream = null
    try {
      out = new BufferedOutputStream(new FileOutputStream(FILENAME))
      toSave.store(out, null)
      out.close()
    } catch {
      case e: IOException =>
        error("Cannot save preferences", e)
    } finally {
      Closeables.closeQuietly(out)
    }
  }
}
