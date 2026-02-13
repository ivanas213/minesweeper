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
        flagsLeft = () => gameController.getState.flags,
        getHintCoordinates = () => gameController.getHintCoordinates,
        isEnded = () => gameController.isEnded,
        isLost = () => gameController.isLost,
        onNewGame = () => (),
        onRestart = () => (),
        onSaveGame = () => (),
        onLoadSaved = () => (),
        onLoadLevel = () => (),
        onLoadMoves = () => (),
        onShowResults = () => ()
      )
      gameController.setOnTimeChanged(seconds =>
        gameView.updateTime(seconds)
      )
      stage.scene = new Scene {
        root = gameView.root
      }
    }
    val sceneController = new SceneController(stage, startGame)
    sceneController.showDifficultySelection()
   
  }
}

// videti to sto su neki nivoi preveliki
// da li moraju da se potrose zastavice

// restart dugme
// hint logika

// da li da ogranicim broj zastavica

// pracenje rezultata i logika za racunanje rezultata
// validacija nivoa

// da smajlic restartuje

// videti za ovo sto velicina prelazi