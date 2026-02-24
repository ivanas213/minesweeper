package ui.view

import model.{Difficulty, LevelParameters}
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label, ScrollPane}
import scalafx.scene.layout.VBox

import scala.util.Random

class SelectLevelView(
                       difficultyName: String,
                       levels: Vector[LevelParameters],
                       onSelect: LevelParameters => Unit,
                       onBack: () => Unit
                     ) {

  private def primaryButton(text: String)(action: => Unit): Button =
    new Button(text) {
      prefWidth = 220
      style =
        s"""
           -fx-background-color: #2F80ED;
           -fx-text-fill: white;
           -fx-font-size: 14px;
           -fx-font-weight: bold;
           -fx-background-radius: 10;
         """
      onAction = _ => action
    }

  private val levelsBox = new VBox {
    spacing = 10
    alignment = Pos.TopCenter

    children = levels.map { level =>
      primaryButton(level.name) {
        onSelect(level)
      }
    }
  }

  private val scrollPane = new ScrollPane {
    content = levelsBox
    fitToWidth = true
    prefViewportHeight = 300
    style =
      """
        -fx-background-color: transparent;
        -fx-border-color: transparent;
      """
  }

  val root: VBox = new VBox {
    alignment = Pos.Center
    spacing = 18
    style =
      s"""
         -fx-background-color: #F4F6FA;
         -fx-padding: 40;
       """

    children = Seq(

      new Label("Изабери ниво") {
        style =
          s"""
             -fx-font-size: 22px;
             -fx-font-weight: bold;
             -fx-text-fill: #2F80ED;
           """
      },

      new Label(difficultyName) {
        style =
          """
            -fx-font-size: 14px;
            -fx-text-fill: #555555;
          """
      },

      primaryButton("🎲 Насумично изабран ниво") { // TODO mozda bolje nesto prirodnije od ove kockice
        onSelect(levels(Random.nextInt(levels.length)))
      },

      scrollPane,

      new Button("Назад") {
        prefWidth = 140
        style =
          """
            -fx-background-color: transparent;
            -fx-text-fill: #555555;
            -fx-font-size: 13px;
          """
        onAction = _ => onBack()
      }
    )
  }
}
