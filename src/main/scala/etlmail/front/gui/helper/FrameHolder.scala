package etlmail.front.gui.helper

import javax.swing.JFrame
import javax.swing.JMenuBar

import org.springframework.beans.factory.annotation.Autowired

import etlmail.front.gui.application.WindowJanitor
import etlmail.front.gui.helper.Edt._

class FrameHolder {
  val frame = makeFrame()

  def makeFrame(): JFrame = invokeAndWait(new JFrame)

  @Autowired
  private def register(janitor: WindowJanitor) {
    janitor.register(frame)
  }

  def show() {
    frame.pack()
    frame.setLocationByPlatform(true)
    frame.setVisible(true)
  }

  def setJMenuBar(menuBar: JMenuBar) {
    frame.setJMenuBar(menuBar)
  }
}