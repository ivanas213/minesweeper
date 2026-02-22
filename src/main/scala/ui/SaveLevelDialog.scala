package ui

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font
import scalafx.stage.{Modality, Stage}
import utilities.ButtonStyles

class SaveLevelDialog(
                     onSave: String => Unit
                     ){
  def show(): Unit = {

    val stage = new Stage {
      title = "Сачувај резултат"
      initModality(Modality.ApplicationModal)
    }

    val name = new TextField {
      promptText = "назив игре"
      minWidth = 260
    }

    val save = new Button("Сачувајте игру") {
      minWidth = 260
      minHeight = 42
      font = Font.font(15)
      style = ButtonStyles.ButtonClassic
      disable = true

      onAction = _ => {
        onSave(name.text.value.trim)
        stage.close()
      }
    }

    name.text.onChange { (_, _, newValue) =>
      save.disable = newValue.trim.isEmpty
    }

    val root = new VBox {
      spacing = 18
      padding = Insets(30)
      alignment = Pos.Center

      children = Seq(

       
        new Label("Унесите назив игре") {
          font = Font.font(14)
        },

        name,
        save
      )
    }

    stage.scene = new Scene(root)
    stage.show()
  }
}
