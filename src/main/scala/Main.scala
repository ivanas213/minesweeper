import logic.{GameController, Level}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import ui.{GameView, SceneController}


object Main extends JFXApp3  {
  override def start(): Unit = {
    val stage = new PrimaryStage

    def startGame(level: Level): Unit = {
      val gameController = new GameController(level.path)

      val gameView = new GameView(
        rows = gameController.rows,
        cols = gameController.cols,
        onLeft = gameController.onLeftClick,
        onRight = gameController.onRightClick,
        getCellView = gameController.getCellView,
        flagsLeft = gameController.getState.flags,
        onHint = () => ()
      )

      stage.scene = new Scene {
        root = gameView.root
      }
    }
    val sceneController = new SceneController(stage, startGame)
    sceneController.showDifficultySelection()

  }
}

