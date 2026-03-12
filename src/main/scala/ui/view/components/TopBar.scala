package ui.view.components

import scalafx.geometry.Pos
import scalafx.geometry.Pos.Center
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.text.Font
import utilities.style.{Graphics, Images}

class TopBar(flagsLeft: Int, onHint: () => Unit, onRestart: () => Unit) {

  private val smileView = Graphics().SmileView
  private val flagsIcon = new ImageView(Images.FlagImg) {
    fitWidth = 14
    fitHeight = 14
    preserveRatio = true
  }

  private val flagsView = new Label(s" $flagsLeft") {
    font = Font.font(14)
    graphic = flagsIcon
  }

  private val clockIcon = new ImageView(Images.Clock) {
    fitWidth = 14
    fitHeight = 14
    preserveRatio = true
  }

  private val timeView = new Label(" 000") {
    font = Font.font(14)
    graphic = clockIcon
  }
  def showHappy(): Unit =
    smileView.image = Images.HappySmileImg

  def showSad(): Unit =
    smileView.image = Images.SadSmileImg

  def showVeryHappy(): Unit =
    smileView.image = Images.VeryHappyImg

  private val centerView: HBox = {
    val smileButton = new Button {
      graphic = smileView
      style = "-fx-background-color: transparent;"
      onAction = _ => onRestart()
    }
    val hintButton = new Button {
      graphic = Graphics().HintView
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
    flagsView.text = s" ${flags}"
  }

  def setTime(time: Int): Unit = {
    timeView.text = s" %03d".format(time)
  }
}
