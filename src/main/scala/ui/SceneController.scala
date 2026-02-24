package ui

import model.{Difficulty, Level, LevelParameters}
import scalafx.scene.{Parent, Scene}
import scalafx.stage.Stage
import ui.view.{SelectDifficultyView, SelectLevelView, SelectSavedGameView, StartView}

import java.time.LocalDateTime

class SceneController(
                       stage: Stage,
                       onGameLevelSelected: LevelParameters => Unit,
                       onEditLevelSelected: LevelParameters => Unit,
                       getSavedGames: () => Seq[(String, LocalDateTime)],
                       getLevels: Difficulty => Vector[LevelParameters],
                       onGameSelected: String => Unit,
                     ) {

  private def setScene(sceneRoot: Parent): Unit =
    stage.scene = new Scene { this.root = sceneRoot }
  private def showGameDifficultySelection(): Unit =
    setScene(new SelectDifficultyView(showGameLevelSelection, getLevels).root)
  private def showEditLevelDifficultySelection(): Unit =
    setScene(new SelectDifficultyView(showEditLevelSelection, getLevels).root)
  private def showGameLevelSelection(difficulty: String, levels:Vector[LevelParameters]): Unit =
    setScene(new SelectLevelView(difficulty, levels, onGameLevelSelected, showGameDifficultySelection).root)
  private def showEditLevelSelection(difficulty: String, levels: Vector[LevelParameters]): Unit =
    setScene(new SelectLevelView(difficulty, levels, onEditLevelSelected, showGameDifficultySelection).root)
  private def showSavedLevels(): Unit =
    setScene(new SelectSavedGameView(onGameSelected, showStartGame, getSavedGames).root)
  def showStartGame(): Unit =
    setScene(new StartView(() => showGameDifficultySelection(), showSavedLevels, () => showEditLevelDifficultySelection()).root)
  
}