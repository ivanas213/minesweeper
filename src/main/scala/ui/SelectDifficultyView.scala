package ui

import logic.{Beginner, Difficulty, Expert, Intermediate, Level}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font
import utilities.{ButtonStyles, Images}

class SelectDifficultyView(onSelect: (difficulty: String, levels:Vector[Level]) => Unit, levels:  Difficulty => Vector[Level]) {

  private def difficultyButton(text: String, diff: Difficulty): Button =
    new Button(text) {
      minWidth = 240
      minHeight = 45
      font = Font.font(15)
      style = ButtonStyles.ButtonClassic
      onMouseEntered = _ =>
        style = ButtonStyles.ButtonMouseEntered
      onMouseExited = _ =>
        style = ButtonStyles.ButtonMouseExited
      onAction = _ => onSelect(diff.name, levels(diff))
    }

  private val icon = new ImageView(
    Images.DifficultyImg
  ) {
    fitWidth = 64
    fitHeight = 64
    preserveRatio = true
  }

  val root: VBox = new VBox {
    alignment = Pos.Center

    children = Seq(
      new VBox {
        spacing = 25
        padding = Insets(35)
        alignment = Pos.Center



        children = Seq(
          icon,

          new Label("Изаберите тежину") {
            font = Font.font(22)
            style = "-fx-text-fill: #0d47a1;"
          },

          new VBox {
            spacing = 15
            alignment = Pos.Center
            children = Seq(
              difficultyButton("Почетни", Beginner),
              difficultyButton("Средњи", Intermediate),
              difficultyButton("Напредни", Expert)
            )
          }
        )
      }
    )
  }
}
