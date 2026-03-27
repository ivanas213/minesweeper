package ui.view

import javafx.stage.Window
import model.{Difficulty, Score}
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.stage.{FileChooser, Stage}
import ui.view.BoardView
import ui.*
import ui.dialog.{SaveGameDialog, SaveResultDialog}
import ui.view.components.{TopBar, TopMenu}
import utilities.style.CellStyles

import java.io.File

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
                onLoadMoves: File => Unit,
                getScore: () => Int,
                getTime: () => Int,
                getClicks: () => Int,
                getDifficulty: () => Difficulty,
                onSaveResult: (Difficulty, String, Int) => Unit,
                loadResults: Difficulty => Seq[Score],
                onResize: () => Unit,
                onMakeNewLevel: () => Unit
              ) {

  private def chooseFile(window: Window): Option[File] = {
    val fileChooser = new FileChooser {
      title = "Изаберите фајл са потезима"
      extensionFilters.add(
        new FileChooser.ExtensionFilter("Text files", "*.txt")
      )
    }
    Option(fileChooser.showOpenDialog(window))
  }
  def onLeftClick(row: Int, col: Int): Unit = {
    onLeft(row, col)
    if (isEnded())
      if (isLost())
        topBar.showSad()
      else
        topBar.showVeryHappy()
        val dialog = new SaveResultDialog(getScore(), getTime(), getClicks(), getDifficulty(), onSaveResult)
        dialog.show()
  }
  def onRightClick(row: Int, col: Int): Unit ={ 
    onRight(row, col)
    topBar.setFlags(flagsLeft())
  }
  def updateTime(time: Int): Unit = topBar.setTime(time)
  private def onLoadMovesWithDialog(): Unit = {
    val file = chooseFile(root.scene().getWindow)
    onLoadMoves(file.get)
    boardView.refreshUI()
  }
  def onHint(): Unit = {
    val coordinates = getHintCoordinates()
    
    coordinates match
      case Some(row, col) =>
        boardView.refreshUI()
        boardView.buttons(row)(col).style = CellStyles().Hint
          
          
      case None => 
  }
  private val boardView = new BoardView(rows, cols, onLeftClick, onRightClick, getCellView, onResize)
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
  private val menu = new TopMenu(onNewGame, onRestartAll, onSave, onLoadSaved, onLoadMovesWithDialog, onMakeNewLevel, getDifficulty, loadResults)
  val root: BorderPane = new BorderPane {
    top = new VBox(menu.menuBar, topBar.view)
    center = boardView.grid
  }

}