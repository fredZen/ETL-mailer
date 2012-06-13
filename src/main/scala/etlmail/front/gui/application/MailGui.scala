package etlmail.front.gui.application

import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import javax.swing._

import org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.stereotype.Component

import etlmail.front.gui.helper.FrameHolder
import etlmail.front.gui.mainframe.MainFrame
import grizzled.slf4j.Logging

@Component
class MailGui extends Runnable with Logging {
  import MailGui._

  @Autowired
  private var macListener: MacListener = _
  @Autowired
  private var frame: MainFrame = _
  @Autowired
  private var eventHandler: ApplicationEventHandler = _

  def run() {
    if (isMac) {
      macListener.enable()
    } else {
      addMenuBar(frame)
    }
    frame.show()
    debug("Up and running")
  }

  private def addMenuBar(frame: FrameHolder) {
    val menuBar = new JMenuBar
    val menu = new JMenu("File")
    menuBar.add(menu)
    val aboutMenuItem = new JMenuItem("About")
    aboutMenuItem.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        eventHandler.showAbout()
      }
    })
    val preferencesMenuItem = new JMenuItem("Preferences")
    preferencesMenuItem.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        eventHandler.showPreferences()
      }
    })
    val exitMenuItem = new JMenuItem("Exit")
    exitMenuItem.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        eventHandler.shutDown(e)
      }
    })
    menu.add(aboutMenuItem)
    menu.add(preferencesMenuItem)
    menu.add(exitMenuItem)
    frame.setJMenuBar(menuBar)
  }
}

object MailGui extends App with Logging {

  private def isMac = System.getProperty("mrj.version") != null

  debug("Starting")
  if (isMac) {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ETL Mail")
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  }

  UIManager.setLookAndFeel(lookAndFeel)
  val ctx = new AnnotationConfigApplicationContext(classOf[GuiAppCtx])
  SwingUtilities.invokeLater(ctx.getBean(classOf[MailGui]))

  private def lookAndFeel(): String = {
    for (laf <- UIManager.getInstalledLookAndFeels) {
      if ("Nimbus".equals(laf.getName)) {
        return laf.getClassName
      }
    }
    return classOf[SubstanceBusinessLookAndFeel].getName
  }
}
