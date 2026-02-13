package logic

import model.{Board, GameStatus, Hidden, Lost, Mine, Number, Playing, Revealed, Won}


case class GameState (
                     board: Board,
                     status: GameStatus = Playing,
                     flags: Int,
                     clicks: Int = 0,
                     totalHintsUsed: Int = 0,
                     probabilisticHintsUsed:Int = 0
                     ){
  
  private def checkWin(board: Board): GameStatus =
    val rows = board.cells.indices
    val cols = board.cells.head.indices

    val allNonMinesRevealed =
      rows.forall { r =>
        cols.forall { c =>
          board.cellAt(r, c) match
            case Some(Mine) =>
              true 
            case Some(Number(_)) =>
              board.cellStatusAt(r, c).contains(Revealed)
            case _ => false
        }
      }

    if allNonMinesRevealed then Won else Playing

  

  private def revealZeroNeighbors(
                              board: Board,
                              row: Int,
                              col: Int,
                              visited: Set[(Int, Int)]
                            ): Board = {

    if (visited.contains((row, col)) || !board.cellStatusAt(row, col).contains(Hidden)) return board

    board.cellAt(row, col) match
      case Some(Number(0)) =>
        val newBoard =
          board.changeStatus(row, col)(_ => Revealed)

        board.neighbors(row, col).foldLeft(newBoard) {
          case (b, (r, c)) =>
            revealZeroNeighbors(b, r, c, visited + ((row, col)))
        }

      case Some(Number(_)) =>
        board.changeStatus(row, col)(_ => Revealed)

      case _ =>
        board
  }
  
  def toggleFlag(row: Int, col: Int): GameState = {
    val newBoard = board.changeStatus(row, col)(cell => cell.toggleFlag())
    copy( board = newBoard)
  }
  
  def revealCell(row: Int, col: Int): GameState = {

    board.cellAt(row, col) match
      case None => this
      case Some(cell) if !board.cellStatusAt(row, col).contains(Hidden) =>
        this
      case Some(cell) =>
        cell match
          case Mine =>
            val newBoard: Board = board.changeStatus(row, col)(_ => Revealed)
            copy(
              board = newBoard,
              status = Lost
            )
          case Number(0) =>
            val newBoard =
              revealZeroNeighbors(board, row, col, Set.empty)

            copy(
              board = newBoard,
              status = checkWin(newBoard)
            )

          case Number(_) =>
                val newBoard = board.changeStatus(row, col)(_ => Revealed)
                copy(
                  board = newBoard,
                  status = checkWin(newBoard)
                )
    
  }

  def incrementProbabilisticHints: GameState = {
    copy (probabilisticHintsUsed = probabilisticHintsUsed + 1)
  }

  def totalProbabilisticHints: GameState = {
    copy (totalHintsUsed = totalHintsUsed + 1)
  }
  

  

  

 
}
