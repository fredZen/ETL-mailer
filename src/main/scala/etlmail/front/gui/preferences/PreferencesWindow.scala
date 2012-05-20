package etlmail.front.gui.preferences

import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE

import java.awt.Container

import javax.annotation.PostConstruct
import javax.swing._

import net.miginfocom.swing.MigLayout

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import etlmail.front.gui.helper.FrameHolder
import etlmail.front.gui.helper.Edt._

@Component
@Scope(SCOPE_PROTOTYPE)
class PreferencesWindow extends FrameHolder {
  @Autowired
  private var serverConfiguration: SwingServerConfiguration = _

  @PostConstruct
  def init() = invokeAndWait {
    frame.setTitle("Preferences")
    makeLayout(frame.getContentPane)
    frame.setResizable(false)
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  }

  private def makeLayout(container: Container) {
    container.setLayout(new MigLayout( //
      "fill", //
      "[trailing][leading,grow,fill]", //
      ""))

    container.add(new JLabel("Host"))
    container.add(new JTextField(serverConfiguration.hostDocument, null, 20), "wrap")

    container.add(new JLabel("Port"))
    container.add(new JTextField(serverConfiguration.portDocument, null, 20), "wrap")

    container.add(new JLabel("User"))
    container.add(new JTextField(serverConfiguration.usernameDocument, null, 20), "wrap")
  }
}
