package ui.dialog

import logic.ScoreSaverLoader
import model.{Beginner, Difficulty, Expert, Intermediate}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ComboBox, Label}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font
import scalafx.stage.{Modality, Stage}
import utilities.style.ButtonStyles

class ShowResultsDialog(initialDifficulty: Difficulty, loadResults: Difficulty => Seq[model.Score]) {

  private val scoreLoader = new ScoreSaverLoader()

  def show(): Unit = {

    val stage = new Stage {
      title = "Најбољи резултати"
      initModality(Modality.ApplicationModal)
    }


    val difficulties = ObservableBuffer("Почетни", "Средњи", "Тежи")

    val difficultyBox = new ComboBox[String](difficulties) {
      value = initialDifficulty match
        case Beginner => "Почетни"
        case Intermediate => "Средњи"
        case Expert => "Тежи"
        case _ => throw new Exception("Unknown difficulty")
      minWidth = 220
    }

    
    val resultsBox = new VBox {
      spacing = 10
      alignment = Pos.Center
    }
   
  
    def refreshResults(difficulty: Difficulty): Unit = {
      val results = loadResults(difficulty)

      resultsBox.children.clear()

      if (results.isEmpty) {
        resultsBox.children.add(
          new Label("Нема сачуваних резултата") {
            font = Font.font(14)
          }
        )
      } else {
        results.zipWithIndex.foreach { case (score, index) =>
          resultsBox.children.add(
            new Label(s"${index + 1}. ${score.name} — ${score.score}") {
              font = Font.font(14)
            }
          )
        }
      }
    }

    refreshResults(initialDifficulty)

    difficultyBox.onAction = _ => {
      val selected = difficultyBox.value.value match
        case "Почетни"     => Beginner
        case "Средњи" => Intermediate
        case "Тежи"       => Expert
        case _ => throw new Exception("Unknown difficulty")
      refreshResults(selected)
    }

    val closeButton = new Button("Затвори") {
      minWidth = 200
      minHeight = 38
      font = Font.font(14)
      style = ButtonStyles.ButtonClassic
      onAction = _ => stage.close()
    }

    val root = new VBox {
      spacing = 18
      padding = Insets(30)
      alignment = Pos.Center

      children = Seq(
        new Label("Топ 5 резултата") {
          font = Font.font(20)
        },
        difficultyBox,
        resultsBox,
        closeButton
      )
    }

    stage.scene = new Scene(root)
    stage.show()
  }

}
