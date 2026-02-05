package ui

import scalafx.scene.layout.GridPane
import utilities.CellStyles

class BoardView(
                 rows: Int,
                 cols: Int,
                 onLeft: (Int, Int) => Unit,
                 onRight: (Int, Int) => Unit,
                 getCellView: (Int, Int) => CellView
               ) {
  val grid: GridPane = new GridPane
  val buttons: Array[Array[ButtonCell]] =
    Array.ofDim(rows, cols)
    
  private def onLeftClick(row: Int, col: Int): Unit = {
    onLeft(row, col)
    refreshUI()
  }

  private def onRightClick(row: Int, col: Int): Unit = {
    onRight(row, col)
    refreshUI()
  }
  for (r <- 0 until rows; c <- 0 until cols) {
    val btn = new ButtonCell(r, c, onLeftClick, onRightClick)
    buttons(r)(c) = btn
    grid.add(btn, c, r)
  }

  def markHint(row: Int, col: Int): Unit = {
    buttons(row)(col).style = new CellStyles().Hint
  }

  def disableButtons(): Unit = {
    buttons.foreach { row =>
      row.foreach { button => {
        button.onAction = null
        button.onMouseClicked = null
      }
      }
    }
  }

  private def updateButton(row: Int, col: Int): Unit = {
    val cellView = getCellView(row, col)
    val btn = buttons(row)(col)
    btn.text = cellView.text
    btn.style = cellView.style
    btn.graphic = cellView.graphic.orNull
  }

  private def refreshUI(): Unit = {

    for {
      row <- 0 until rows
      col <- 0 until cols
    } {
      updateButton(row, col)
    }
  }
}
