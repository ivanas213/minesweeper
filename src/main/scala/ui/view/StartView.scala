package ui.view

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font
import utilities.style.{ButtonStyles, Images}

class StartView(
                     onNewGame: () => Unit,
                     onLoadGame: () => Unit,
                     onEditLevel: () => Unit
                   ) {

  private def menuButton(text: String, action: () => Unit): Button =
    new Button(text) {
      minWidth = 240
      minHeight = 50
      font = Font.font(16)
      style = ButtonStyles.ButtonClassic

      onMouseEntered = _ =>
        style = ButtonStyles.ButtonMouseEntered

      onMouseExited = _ =>
        style = ButtonStyles.ButtonMouseExited

      onAction = _ => action()
    }

  private val icon = new ImageView(
    Images.StartGame
  ) {
    fitWidth = 72
    fitHeight = 72
    preserveRatio = true
  }

  val root: VBox = new VBox {
    alignment = Pos.Center

    children = Seq(
      new VBox {
        spacing = 30
        padding = Insets(40)
        alignment = Pos.Center

        children = Seq(
          icon,

          new Label("МИНЕ") { // TODO smisliti lepo ime
            font = Font.font(26)
            style = "-fx-text-fill: #0d47a1;"
          },

          new VBox {
            spacing = 20
            alignment = Pos.Center
            children = Seq(
              menuButton("Нова игра", onNewGame),
              menuButton("Настави игру", onLoadGame),
              menuButton("Направи нови ниво", onEditLevel)
            )
          }
        )
      }
    )
  }
}

