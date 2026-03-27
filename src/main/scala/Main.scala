import logic.controllers.{GameController, LevelController}
import logic.{GameSaverLoader, LevelLoader, ScoreSaverLoader}
import model.{Difficulty, LevelParameters}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.{Parent, Scene}
import ui.view.{GameView, LevelView, SelectDifficultyView, SelectLevelView, SelectSavedGameView, StartView}


object Main extends JFXApp3  {
  override def start(): Unit = {
    val stage = new PrimaryStage

    def setScene(sceneRoot: Parent): Unit =
      stage.scene = new Scene {
        this.root = sceneRoot
      }
    def getLevels(difficulty: Difficulty): Vector[LevelParameters] = LevelLoader.getLevelsByDifficulty(difficulty)
    def getRandomLevel(levels : Vector[LevelParameters]): LevelParameters = LevelLoader.getRandomLevel(levels)
    def showGameDifficultySelection(): Unit =
      setScene(new SelectDifficultyView(showGameLevelSelection,  getLevels).root)

    def showEditLevelDifficultySelection(): Unit =
      setScene(new SelectDifficultyView(showEditLevelSelection, getLevels).root)

    def showGameLevelSelection(difficulty: String, getLevels: () => Vector[LevelParameters]): Unit =
      setScene(new SelectLevelView(difficulty, getLevels, startGame, getRandomLevel, showGameDifficultySelection).root)

    def showEditLevelSelection(difficulty: String, getLevels: () => Vector[LevelParameters]): Unit =
      setScene(new SelectLevelView(difficulty, getLevels, startEditingLevel, getRandomLevel, showGameDifficultySelection).root)

    def showSavedLevels(): Unit =
      setScene(new SelectSavedGameView(loadGame, showStartGame, () => GameSaverLoader.getSavedGamesNames).root)

    def showStartGame(): Unit =
      setScene(new StartView(() => showGameDifficultySelection(), showSavedLevels, () => showEditLevelDifficultySelection()).root)

    def startGame(level: LevelParameters): Unit = {
      val gameController = new GameController(levelPath = Some(level.path))

      val gameView = new GameView(
        rows = gameController.rows,
        cols = gameController.cols,
        onLeft = gameController.leftClick,
        onRight = gameController.rightClick,
        getCellView = gameController.getCellView,
        flagsLeft = () => gameController.getState.flags,
        getHintCoordinates = () => gameController.getHintCoordinates,
        isEnded = () => gameController.isEnded,
        isLost = () => gameController.isLost,
        onNewGame = () => showGameDifficultySelection(),
        onRestart = () => gameController.restart(),
        onSaveGame = gameController.saveGame,
        onLoadSaved = () => showSavedLevels(),
        onLoadMoves = gameController.loadMoves,
        getScore = () => gameController.getScore,
        getTime = () => gameController.getTime,
        getClicks = () => gameController.getClicks,
        getDifficulty = () => gameController.getDifficulty,
        onSaveResult = (difficulty, name, score) => ScoreSaverLoader().saveResult(difficulty, name, score),
        loadResults = difficulty => ScoreSaverLoader().loadResults(difficulty),
        onResize = () => {
          stage.sizeToScene()
          stage.centerOnScreen()
        },
        onMakeNewLevel = () => showEditLevelDifficultySelection()
      )
      gameController.setOnTimeChanged(seconds =>
        gameView.updateTime(seconds)
      )
      stage.scene = new Scene {
        root = gameView.root
      }
      stage.sizeToScene()
      stage.centerOnScreen()
    }

    def startEditingLevel(level: LevelParameters): Unit = {
      val levelController = new LevelController(level.path)

      val levelView = new LevelView(
        onLeft = levelController.toggleCell,
        onAddRowFirst = () => levelController.addEmptyRowFirst(),
        onAddRowLast = () => levelController.addEmptyRowLast(),
        onAddColFirst = () => levelController.addEmptyColumnFirst(),
        onAddColLast = () => levelController.addEmptyColumnLast(),
        onRemoveRowFirst = () => levelController.removeFirstRow(),
        onRemoveRowLast = () => levelController.removeLastRow(),
        onRemoveColFirst = () => levelController.removeFirstColumn(),
        onRemoveColLast = () => levelController.removeLastColumn(),
        onClearRectangle = levelController.clearRectangle,
        onSave = levelController.saveLevel,
        getCellView = levelController.getLevelCellView,
        getRows = () => levelController.rows,
        getCols = () => levelController.cols,
        isValid = () => levelController.isLevelValid,
        onBack = () => showStartGame(),
        onApplyRotation = levelController.applyRotation,
        onApplyReflection = levelController.applyReflection,
        onApplyCentralSymmetry = levelController.applyCentralSymmetry,
        onApplyTranslation = levelController.applyTranslation,
        onAddRotation = levelController.addRotation,
        onAddReflection = levelController.addReflection,
        onAddCentralSymmetry = levelController.addCentralSymmetry,
        onAddTranslation = levelController.addTranslation,
        isometryStepNames = () => levelController.getIsometryStepNames,
        onSaveCustomIsometry = levelController.saveCurrentIsometry,
        savedIsometryNames = () => levelController.getSavedIsometryNames,
        onApplySavedIsometry = levelController.applySavedIsometry,
        resetSelectedIsometry =  () => levelController.resetIsometry(),
        onResize = () => {
          stage.sizeToScene()
          stage.centerOnScreen()
        }
      )
      
      stage.scene = new Scene {
        root = levelView.root
      }
      stage.sizeToScene()
      stage.centerOnScreen()
    }
    def loadGame(name:String): Unit = {
      val gameController = new GameController(initialGameState = Some(GameSaverLoader.loadGame(name)._1) )

      val gameView = new GameView(
        rows = gameController.rows,
        cols = gameController.cols,
        onLeft = gameController.leftClick,
        onRight = gameController.rightClick,
        getCellView = gameController.getCellView,
        flagsLeft = () => gameController.getState.flags,
        getHintCoordinates = () => gameController.getHintCoordinates,
        isEnded = () => gameController.isEnded,
        isLost = () => gameController.isLost,
        onNewGame = () => showGameDifficultySelection(),
        onRestart = () => gameController.restart(),
        onSaveGame = gameController.saveGame,
        onLoadSaved = () => showSavedLevels(),
        onLoadMoves = gameController.loadMoves,
        getScore = () => gameController.getScore,
        getTime = () => gameController.getTime,
        getClicks = () => gameController.getClicks,
        getDifficulty = () => gameController.getDifficulty,
        onSaveResult = (difficulty, name, score) => ScoreSaverLoader().saveResult(difficulty, name, score),
        loadResults = difficulty => ScoreSaverLoader().loadResults(difficulty),
        onResize = () => {
          stage.sizeToScene()
          stage.centerOnScreen()
        },
        onMakeNewLevel = () => showEditLevelDifficultySelection()
      )
      gameController.setOnTimeChanged(seconds =>
        gameView.updateTime(seconds)
      )
      stage.scene = new Scene {
        root = gameView.root
      }
    }
    showStartGame()

  }
}

