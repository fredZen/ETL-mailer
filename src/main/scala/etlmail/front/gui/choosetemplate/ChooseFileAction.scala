package etlmail.front.gui.choosetemplate

import java.awt.Container
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class ChooseFileAction(fileChooser: FileDocumentChooser, frame: Container) extends ActionListener {
  def actionPerformed(event: ActionEvent) {
    fileChooser.showOpenDialog(frame)
  }
}