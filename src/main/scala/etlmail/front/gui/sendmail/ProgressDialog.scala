package etlmail.front.gui.sendmail

import java.awt.Frame
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

import javax.swing._

import net.miginfocom.swing.MigLayout

import org.springframework.beans.factory.annotation.Autowired

import etlmail.front.gui.application.WindowJanitor

@SuppressWarnings(Array("serial"))
class ProgressDialog(parent: Frame, worker: SwingWorker[_, _]) extends JDialog(parent, "Sending mail", true) {

  setLayout(new MigLayout)
  setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
  add(progressBar())
  pack()
  addComponentListener(new ComponentAdapter() {
    override def componentShown(e: ComponentEvent) {
      worker.execute()
    }
  })

  private def progressBar(): JProgressBar = {
    val progressBar = new JProgressBar
    progressBar.setString("Sending")
    progressBar.setIndeterminate(true)
    progressBar.setStringPainted(true)
    return progressBar
  }

  @Autowired
  def register(janitor: WindowJanitor) {
    janitor.register(this)
  }
}