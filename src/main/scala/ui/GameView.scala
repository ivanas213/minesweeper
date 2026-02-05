package ui

import scalafx.scene.layout.VBox

class GameView(
                rows: Int,
                cols: Int,
                onLeft: (Int, Int) => Unit,
                onRight: (Int, Int) => Unit,
                getCellView: (Int, Int) => CellView,
                flagsLeft: Int,
                onHint: () => Unit
              ) {
  private val boardView = new BoardView(rows, cols, onLeft, onRight, getCellView)
  private val topBar = new TopBar(flagsLeft, onHint)
  val root: VBox = new VBox{
    spacing = 15
    children = Seq(topBar.view, boardView.grid)
  }
}