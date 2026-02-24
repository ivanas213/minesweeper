package logic.controllers

import logic.*
import model.*
import services.GameTimer
import ui.view.*

import java.io.File
import scala.compiletime.uninitialized

class GameController(levelPath: Option[String] = None, initialGameState: Option[GameState] = None) {

  
  private var state: GameState = levelPath match {
    case Some(path) =>
      val board = LevelLoader.loadGame(path)
      val flags = board.countMines
      GameState(board = board, flags = flags)

    case None =>
      initialGameState.get
  }
  private val timer = new GameTimer(seconds =>
    state = state.copy(time = seconds)
    if (onTimeChanged != null)
      onTimeChanged(seconds)
  )
  private val gameSaverLoader = GameSaverLoader
  private var onTimeChanged: Int => Unit = uninitialized

  def setOnTimeChanged(callback: Int => Unit): Unit =
    onTimeChanged = callback

  def onLeftClick(row: Int, col: Int): Unit = {
    if (state.status == Playing)
      state = state.revealCell(row, col)
      incrementClicks()

  }

  def onRightClick(row: Int, col: Int): Unit = {
    if (state.status == Playing)
      if !state.board.cellStatusAt(row, col).contains(Flagged) then
        decrementFlags()
      else
        incrementFlags()
      incrementClicks()
      state = state.toggleFlag(row, col)
  }

  def restart(): Unit = {
    resetTimer()
    timer.start()
    state = levelPath match {
      // TODO mozda izvuci ipak kao posebnu metodu
      case Some(path) =>
        val board = LevelLoader.loadGame(path) // TODO ovo bi moglo i malo bolje sig bez da se ucitava i ovde i tamo
        val flags = board.countMines

        GameState(board = board, flags = flags, onEnd = () => timer.stop())
      case None =>
        initialGameState.get
    }
  }

  def loadMoves(file: File): Unit = {
    val moves = MoveLoader.loadMoves(file)
    for (move <- moves) {
      if (move.moveType == MoveType.Left)
        onLeftClick(move.row, move.col)
      else
        onRightClick(move.row, move.col)
    }
  }

  def saveGame(name: String): Unit = gameSaverLoader.saveGame(name, state)

  def getTime: Int = state.time
  
  def getClicks: Int = state.clicks
  
  def getScore: Int = state.getScore
  
  def getDifficulty: Difficulty = state.board.difficulty

  def getHintCoordinates: Option[(Int, Int)] = {
    if (state.status == Playing)
      val hint = state.board.getSafeCell
      hint match
        case Some(r: Int, c: Int) =>
          incrementTotalHintsUsed()
          Some(r, c)
        case None =>
          val random = state.board.getMaybeSafeCell
          random match
            case Some(r: Int, c: Int) =>
              incrementProbablisticHintsUsed()
              Some(r, c)
            case None =>
              None
    else
      None
  }

  def getCellView(row: Int, col: Int): CellView = {
    val cell = state.board.cellAt(row, col)
    val cellStatus = state.board.cellStatusAt(row, col)
    val gameStatus = state.status
    gameStatus match
      case Playing =>
        cellStatus match
          case Some(Flagged) => FlaggedCellView()
          case Some(Revealed) =>
            cell match
              case Some(model.Number(value)) => EmptyRevealedCellView(value)
              case _ => HiddenCellView()
          case _ => HiddenCellView()
      case _ =>
        cellStatus match
          case Some(Flagged) => FlaggedCellView()
          case Some(Revealed) =>
            cell match
              case Some(model.Number(value)) => EmptyRevealedCellView(value)
              case _ => MineCellView()
          case _ =>
            cell match
              case Some(model.Number(value)) => HiddenCellView()
              case _ => MineToRevealCellView()
  }

  def rows: Int = state.board.rows

  def cols: Int = state.board.cols
  
  def getState: GameState = state

  def isLost: Boolean = state.status == Lost

  def isEnded: Boolean = state.status != Playing

  private def incrementClicks(): Unit = state = state.copy(clicks = state.clicks + 1)

  private def incrementFlags(): Unit = state = state.copy(flags = state.flags + 1)

  private def decrementFlags(): Unit = state = state.copy(flags = state.flags - 1)

  private def incrementProbablisticHintsUsed(): Unit = state = state.copy(probabilisticHintsUsed = state.probabilisticHintsUsed + 1)

  private def incrementTotalHintsUsed(): Unit = state = state.copy(totalHintsUsed = state.totalHintsUsed + 1)
  private def resetTimer(): Unit = timer.reset()

  private def stopTimer(): Unit = timer.stop()


  timer.resetTo(state.time)
  timer.start()
  state = state.copy(onEnd = () => timer.stop())
  
  
  

 
  

  
  
}
