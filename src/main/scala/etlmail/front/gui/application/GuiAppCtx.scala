package etlmail.front.gui.application

import javax.swing.SwingWorker

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation._

import etlmail.front.gui.helper.FrameHolder
import etlmail.front.gui.sendmail.ProgressDialog
import etlmail.front.gui.sendmail.SendMailAction
import etlmail.front.gui.mainframe.MainFrame

@Configuration
@ComponentScan(basePackageClasses = Array(
  classOf[etlmail.front.gui.ComponentScanMarker],
  classOf[etlmail.context.ComponentScanMarker]))
class GuiAppCtx {
  @Bean
  @Autowired
  def sendMailAction(mainFrame: MainFrame): SendMailAction = new SendMailAction() {
    protected def makeProgressDialog(sendMailWorker: SwingWorker[_, _]): ProgressDialog =
      new ProgressDialog(mainFrame.frame, sendMailWorker)
  }
}
