package ui.view

import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label, ScrollPane}
import scalafx.scene.layout.VBox
import utilities.style.ButtonStyles

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SelectSavedGameView(
                           onSelect: String => Unit,
                           onBack: () => Unit,
                           getSavedGames: () => Seq[(String, LocalDateTime)]
                         ) {


  private val savedGames = getSavedGames()
  private val timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
  private val saveBox = new VBox {
    spacing = 15
    alignment = Pos.TopCenter

    private def saveCard(name: String, date: LocalDateTime): VBox =
      new VBox {
        spacing = 6
        prefWidth = 320
        alignment = Pos.CenterLeft

        style =
          """
          -fx-background-color: white;
          -fx-background-radius: 12;
          -fx-padding: 15;
          -fx-border-color: #E0E6F0;
          -fx-border-radius: 12;
        """

        children = Seq(

          new Label(name.toUpperCase) {
            style =
              """
              -fx-font-size: 16px;
              -fx-font-weight: bold;
              -fx-text-fill: #2F80ED;
            """
          },

          new Label(date.format(timeFormatter)) {
            style =
              """
              -fx-font-size: 12px;
              -fx-text-fill: #777777;
            """
          }
        )

        onMouseClicked = _ => onSelect(name)

        onMouseEntered = _ =>
          style =
            """
            -fx-background-color: #F0F4FF;
            -fx-background-radius: 12;
            -fx-padding: 15;
            -fx-border-color: #2F80ED;
            -fx-border-radius: 12;
          """

        onMouseExited = _ =>
          style =
            """
            -fx-background-color: white;
            -fx-background-radius: 12;
            -fx-padding: 15;
            -fx-border-color: #E0E6F0;
            -fx-border-radius: 12;
          """
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
        savedGames.map { case (name, date) =>
          saveCard(name, date)
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
            -fx-font-size: 26px;
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
} 

// TODO sve ove stilove lepo prepakovati, duplikata da nema, sve srediti
