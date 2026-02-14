package ui

import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label, ScrollPane}
import scalafx.scene.layout.VBox
import utilities.ButtonStyles

import java.io.File

class SelectSavedGameView(
                           onSelect: String => Unit,
                           onBack: () => Unit,
                           getSavedGames: () => Seq[String]
                         ) {


  private val savedGames = getSavedGames()

  private val saveBox = new VBox {
    spacing = 10
    alignment = Pos.TopCenter

    private def primaryButton(text: String)(action: => Unit): Button =
      new Button(text) {
        prefWidth = 220
        style = ButtonStyles.ButtonClassic
        onAction = _ => action
      }
    children =
      if (savedGames.isEmpty)
        Seq(
          new Label("Нема сачуваних игара.") {
            style =
              """
                -fx-font-size: 14px;
                -fx-text-fill: #777777;
              """
          }
        )
      else
        savedGames.map { name =>
          primaryButton(name) {
            onSelect(name)
          }
        }
  }

  private val scrollPane = new ScrollPane {
    content = saveBox
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
      """
        -fx-background-color: #F4F6FA;
        -fx-padding: 40;
      """

    children = Seq(

      new Label("Настави игру") {
        style =
          """
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: #2F80ED;
          """
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
} // TODO sve ove stilove lepo prepakovati, duplikata da nema, sve srediti
