package etlmail.front.gui.about

import java.awt.Container

import javax.annotation.PostConstruct
import javax.swing.JLabel

import net.miginfocom.swing.MigLayout

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import etlmail.front.gui.helper.FrameHolder
import etlmail.front.gui.helper.Edt._

@Component
class AboutWindow extends FrameHolder {
  @Autowired private var imageProvider: ImageProvider = _

  @PostConstruct
  def init() = invokeAndWait {
    makeLayout(frame.getContentPane)
  }

  private def makeLayout(container: Container) {
    container.setLayout(new MigLayout("fill", "10[]20", "push[]push"))

    container.add(imageProvider.madame, "dock west")
    container.add(new JLabel("<html>ETL mail v0.20<br>by FRO, NDN & FME</html>"), "")
  }
}
