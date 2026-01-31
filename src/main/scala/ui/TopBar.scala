package ui

import javafx.geometry
import scalafx.geometry.Pos
import scalafx.geometry.Pos.Center
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.text.Font
import utilities.Graphics.HintView
import utilities.Images.{HappySmileImg, SadSmileImg, VeryHappyImg}

class TopBar(flagsLeft: Int, onHint: () => Unit, onRestart: () => Unit) {

  private val smileView = utilities.Graphics.SmileView
  private val flagsView = new Label(s"🚩 ${flagsLeft}") {
    font = Font.font(14)
  }

  private val timeView = new Label("⏱ 000") {
    font = Font.font(14)
  }
  def showHappy(): Unit =
    smileView.image = HappySmileImg

  def showSad(): Unit =
    smileView.image = SadSmileImg

  def showVeryHappy(): Unit =
    smileView.image = VeryHappyImg

  private val centerView: HBox = {
    val smileButton = new Button {
      graphic = smileView
      style = "-fx-background-color: transparent;"
      onAction = _ => onRestart()
    }
    val hintButton = new Button {
      graphic = HintView
      style = "-fx-background-color: transparent;"
      onAction = _ => onHint()
    }
    new HBox(2) {
      alignment = Pos.Center
      children = Seq(smileButton, hintButton)
    }
  }
  val view: BorderPane = new BorderPane {
    left = flagsView
    center = centerView
    right = timeView
    BorderPane.setAlignment(flagsView, Center)
    BorderPane.setAlignment(centerView, Center)
    BorderPane.setAlignment(timeView, Center)
  }

  def setFlags(flags: Int): Unit = {
    flagsView.text = s"🚩 ${flags}"
  }

  def setTime(time: Int): Unit = {
    timeView.text = s"⏱ %03d".format(time)
  }
}
