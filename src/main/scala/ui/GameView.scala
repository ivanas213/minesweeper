package ui

import scalafx.scene.layout.VBox
import services.GameTimer
import utilities.CellStyles

class GameView(
                rows: Int,
                cols: Int,
                onLeft: (Int, Int) => Unit,
                onRight: (Int, Int) => Unit,
                getCellView: (Int, Int) => CellView,
                flagsLeft: () => Int,
                getHintCoordinates: () => Option[(Int, Int)],
                isEnded: () => Boolean,
                isLost: () => Boolean
              ) {
  
  private val timer = new GameTimer(time =>
    topBar.setTime(time)
  )

  private def startTimer(): Unit = timer.start()
  startTimer()
  private def stopTimer(): Unit = timer.stop()
  def onLeftClick(row: Int, col: Int): Unit = {
    onLeft(row, col)
    if (isEnded())
      stopTimer()
      if (isLost())
        topBar.showSad()
      else
        topBar.showVeryHappy()
  }
  def onRightClick(row: Int, col: Int): Unit ={ 
    onRight(row, col)
    topBar.setFlags(flagsLeft())
  }
  def resetTimer(): Unit = timer.reset()
  def onHint(): Unit = {
    val coordinates = getHintCoordinates()
    
    coordinates match
      case Some(row, col) =>
        boardView.refreshUI()
        boardView.buttons(row)(col).style = CellStyles().Hint
          
      case None => 
  }
  private val boardView = new BoardView(rows, cols, onLeftClick, onRightClick, getCellView)
  private val topBar = new TopBar(flagsLeft(), onHint)
  val root: VBox = new VBox{
    spacing = 15
    children = Seq(topBar.view, boardView.grid)
  }
}