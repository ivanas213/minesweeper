package logic

import model.{Board, Flagged, Hidden, Lost, Mine, Playing, Revealed}
import services.GameTimer
import ui.{CellView, EmptyRevealedCellView, FlaggedCellView, HiddenCellView, MineCellView, MineToRevealCellView}
import scala.compiletime.uninitialized

class GameController(levelPath: Option[String] = None, initialGameState: Option[GameState] = None) {

  private var state: GameState = levelPath match {
    case Some(path) =>
      val board = LevelLoader.loadLevel(path)
      val flags = board.countMines
      GameState(board = board, flags = flags)

    case None =>
      initialGameState.get
  }

  private val gameSaverLoader = GameSaverLoader
  private var onTimeChanged: Int => Unit = uninitialized
  private def resetTimer(): Unit = timer.reset()
  def restart(): Unit = {
    state = levelPath match {
      case Some(path) =>
        val board = LevelLoader.loadLevel(path) // TODO ovo bi moglo i malo bolje sig bez da se ucitava i ovde i tamo
        val flags = board.countMines
        resetTimer()
        timer.start() // TODO mozda izvuci ipak kao posebnu metodu
        GameState(board = board, flags = flags, onEnd = () => timer.stop())
      case None =>
        initialGameState.get
    }
  }
  def setOnTimeChanged(callback: Int => Unit): Unit =
    onTimeChanged = callback

  private val timer = new GameTimer ( seconds =>
    state = state.copy(time = seconds)
    if (onTimeChanged != null)
      onTimeChanged(seconds)
  )

  timer.resetTo(state.time)
  timer.start()
  state = state.copy(onEnd = () => timer.stop())
  def saveGame(name:String): Unit = gameSaverLoader.saveGame(name, state)
  private def stopTimer(): Unit = timer.stop()
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
  def isLost:Boolean = state.status == Lost
  def isEnded: Boolean = state.status != Playing
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
  def getState: GameState = state
  
  private def incrementClicks(): Unit = state = state.copy(clicks = state.clicks + 1)
  private def incrementFlags(): Unit = state = state.copy(flags = state.flags + 1)
  private def decrementFlags(): Unit = state = state.copy(flags = state.flags - 1)
  private def incrementProbablisticHintsUsed(): Unit = state = state.copy(probabilisticHintsUsed = state.probabilisticHintsUsed + 1)
  private def incrementTotalHintsUsed(): Unit = state = state.copy(totalHintsUsed = state.totalHintsUsed + 1)
  def rows: Int = state.board.rows
  def cols: Int = state.board.cols
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

  

  
  
}
