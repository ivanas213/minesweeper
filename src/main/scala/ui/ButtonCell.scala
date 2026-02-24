package ui

import scalafx.scene.control.Button
import scalafx.scene.input.MouseButton
import scalafx.Includes.jfxMouseEvent2sfx
import utilities.style.CellStyles

class ButtonCell (
                 row: Int,
                 col:Int,
                 onLeftClick: (Int, Int) => Unit,
                 onRightClick: (Int, Int) => Unit  
) extends Button{
  prefWidth = 32
  prefHeight = 32
  style = new CellStyles().Empty
  onMouseClicked = event => {
    event.button match
      case MouseButton.Primary =>
        onLeftClick(row, col)
      case MouseButton.Secondary =>
        onRightClick(row, col)
      case _ => ()
  }
}
