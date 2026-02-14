package ui

import logic.{Difficulty, Level}
import scalafx.scene.{Parent, Scene}
import scalafx.stage.Stage

class SceneController(
                       stage: Stage,
                       onLevelSelected: Level => Unit,
                       getSavedGames: () => Seq[String],
                       getLevels: Difficulty => Vector[Level],
                       onGameSelected: String => Unit
                     ) {

  private def setScene(sceneRoot: Parent): Unit =
    stage.scene = new Scene { this.root = sceneRoot }

  private def showDifficultySelection(): Unit =
    setScene(new SelectDifficultyView(showLevelSelection, getLevels).root)

  private def showLevelSelection(difficulty: String, levels:Vector[Level]): Unit =
    setScene(new SelectLevelView(difficulty, levels, onLevelSelected, showDifficultySelection).root)
  private def showSavedLevels(): Unit =
    setScene(new SelectSavedGameView(onGameSelected, showStartGame, getSavedGames).root)
  def showStartGame(): Unit =
    setScene(new StartView(() => showDifficultySelection(), showSavedLevels).root)
  
}