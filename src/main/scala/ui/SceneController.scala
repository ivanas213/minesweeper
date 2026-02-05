package ui

import logic.{Difficulty, Level}
import scalafx.scene.{Parent, Scene}
import scalafx.stage.Stage

class SceneController(
                       stage: Stage,
                       onLevelSelected: Level => Unit
                     ) {

  private def setScene(sceneRoot: Parent): Unit =
    stage.scene = new Scene { this.root = sceneRoot }

  def showDifficultySelection(): Unit =
    setScene(new SelectDifficultyView(showLevelSelection).root)

  private def showLevelSelection(difficulty: Difficulty): Unit =
    setScene(new SelectLevelView(difficulty, onLevelSelected, showDifficultySelection).root)
}