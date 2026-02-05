package logic

import model.{Board, Flagged, Hidden, Mine, Playing, Revealed}
import ui.{CellView, EmptyRevealedCellView, FlaggedCellView, HiddenCellView, MineCellView, MineToRevealCellView}

class GameController(levelPath: String) {

  private var state: GameState = {
    val board: Board = LevelLoader.loadLevel(levelPath)
    val flags = board.countMines
    GameState(board = board, flags = flags)
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

  def getState: GameState = state

  private def incrementClicks(): Unit = state = state.copy(clicks = state.clicks + 1)
  private def incrementFlags(): Unit = state = state.copy(flags = state.flags + 1)
  private def decrementFlags(): Unit = state = state.copy(flags = state.flags - 1)

  def rows: Int = state.board.rows
  def cols: Int = state.board.cols
  def onLeftClick(row: Int, col: Int): Unit = {
    if (state.status == Playing)
      state = state.revealCell(row, col)
      incrementClicks()
  }

  def onRightClick(row: Int, col: Int): Unit = {
    if (state.status == Playing)
      if (state.board.cellStatusAt(row, col) == Flagged) then
        decrementFlags()
      else
        incrementFlags()
      incrementClicks()
      state = state.toggleFlag(row, col)
  }

  def onRestart(): Unit = {

  }

  def onHint(): Unit = {

  }
}
