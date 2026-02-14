package ui

import scalafx.scene.layout.{BorderPane, VBox}
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
                isLost: () => Boolean,
                onNewGame: () => Unit,
                onRestart: () => Unit,
                onSaveGame: String => Unit,
                onLoadSaved: () => Unit,
                onLoadLevel: () => Unit,
                onLoadMoves: () => Unit,
                onShowResults: () => Unit
              ) {
  
  
  def onLeftClick(row: Int, col: Int): Unit = {
    onLeft(row, col)
    if (isEnded())
      if (isLost())
        topBar.showSad()
      else
        topBar.showVeryHappy()
  }
  def onRightClick(row: Int, col: Int): Unit ={ 
    onRight(row, col)
    topBar.setFlags(flagsLeft())
  }
  def updateTime(time: Int): Unit = topBar.setTime(time)
  
  def onHint(): Unit = {
    val coordinates = getHintCoordinates()
    
    coordinates match
      case Some(row, col) =>
        boardView.refreshUI()
        boardView.buttons(row)(col).style = CellStyles().Hint
          
      case None => 
  }
  private val boardView = new BoardView(rows, cols, onLeftClick, onRightClick, getCellView)
  private val topBar = new TopBar(flagsLeft(), onHint, onRestartAll)
  private def onSave(): Unit ={
    val dialog = new SaveGameDialog(name =>
      onSaveGame(name)
    )
    dialog.show()
  }
  private def onRestartAll(): Unit = {
    onRestart()
    boardView.refreshUI()
    topBar.setFlags(flagsLeft())
    topBar.showHappy()
  }
  private val menu = new TopMenu(onNewGame, onRestartAll, onSave, onLoadSaved, onLoadLevel, onLoadSaved, onShowResults)
  val root: BorderPane = new BorderPane {
    top = new VBox(menu.menuBar, topBar.view)
    center = boardView.grid
  }

}