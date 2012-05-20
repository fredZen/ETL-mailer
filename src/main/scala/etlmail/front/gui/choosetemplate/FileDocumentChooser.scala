package etlmail.front.gui.choosetemplate

import java.awt.Component
import java.awt.HeadlessException
import java.io.File

import javax.swing.JFileChooser

class FileDocumentChooser(document: FileDocument) extends JFileChooser {
  import JFileChooser._

  override def showOpenDialog(arg0: Component): Int = {
    setFile()
    val result = super.showOpenDialog(arg0)
    if (result == APPROVE_OPTION) {
      document.setFile(getSelectedFile)
    }
    return result
  }

  private def setFile() {
    val currentFile = document.getFile
    if (currentFile.isFile) {
      setSelectedFile(currentFile)
    } else if (currentFile.isDirectory) {
      setCurrentDirectory(currentFile)
    }
  }
}
