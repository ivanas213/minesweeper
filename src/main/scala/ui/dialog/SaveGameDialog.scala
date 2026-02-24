package ui.dialog

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font
import scalafx.stage.{Modality, Stage}
import utilities.style.ButtonStyles

class SaveGameDialog(onSave: String => Unit) {

  def show(): Unit = {

    val stage = new Stage {
      title = "Сачувај игру"
      initModality(Modality.ApplicationModal)
    }

    val nameField = new TextField {
      promptText = "Унесите назив игре"
      minWidth = 240
    }

    val saveButton = new Button("Сачувај") {
      minWidth = 240
      minHeight = 40
      font = Font.font(15)
      style = ButtonStyles.ButtonClassic
      disable = true

      onAction = _ => {
        onSave(nameField.text.value)
        stage.close()
      }
    }

    nameField.text.onChange { (_, _, newValue) =>
      saveButton.disable = newValue.trim.isEmpty
    }

    val root = new VBox {
      spacing = 20
      padding = Insets(30)
      alignment = Pos.Center

      children = Seq(
        new Label("Назив игре") {
          font = Font.font(18)
        },
        nameField,
        saveButton
      )
    }

    stage.scene = new Scene(root)
    stage.show()
  }
}
