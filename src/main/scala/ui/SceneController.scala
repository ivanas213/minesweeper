package ui

import logic.{Difficulty, Level}
import scalafx.scene.{Parent, Scene}
import scalafx.stage.Stage

class SceneController (stage: Stage){
  
  private def showLevelSelection(difficulty: Difficulty): Unit = {
    val view = new SelectLevelView(difficulty, showGame, showDifficultySelection)
    stage.scene = getSceneByRoot(view.root)
  }

  private def getSceneByRoot(stageRoot: Parent): Scene =
    new Scene {
      root = stageRoot
    }
  
  def showDifficultySelection(): Unit = {
    val view = new SelectDifficultyView(showLevelSelection)
    stage.scene = getSceneByRoot(view.root)
  }

  def showGame(level: Level): Unit = {

  }
}
