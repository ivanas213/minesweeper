import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import ui.SceneController


object Main extends JFXApp3  {
  override def start(): Unit = {
    stage = new PrimaryStage()
    val sceneController = new SceneController(stage)
    sceneController.showDifficultySelection()
  }
}

