package ui.dialog

import model.Difficulty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font
import scalafx.stage.{Modality, Stage}
import utilities.style.ButtonStyles

class SaveResultDialog(
                        score: Int,
                        time: Int,
                        clicks: Int,
                        difficulty: Difficulty,
                        onSave: (Difficulty, String, Int) => Unit
                      ) {

  def show(): Unit = {

    val stage = new Stage {
      title = "Резултат"
      initModality(Modality.ApplicationModal)
    }

    val name = new TextField {
      promptText = "Унесите име"
      minWidth = 260
    }

    val save = new Button("Сачувајте резултат") {
      minWidth = 260
      minHeight = 42
      font = Font.font(15)
      style = ButtonStyles.ButtonClassic
      disable = true

      onAction = _ => {
        onSave(difficulty, name.text.value.trim, score)
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

        new Label("🎉 Честитамо!") {
          font = Font.font(24)
        },

        new Label(s"Освојили сте $score поена") {
          font = Font.font(18)
        },

        new Label(s"Време: $time s") {
          font = Font.font(14)
        },

        new Label(s"Број кликова: $clicks") {
          font = Font.font(14)
        },

        new Label("Унесите име под којим желите да се сачува резултат") {
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
