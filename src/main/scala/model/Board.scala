package model

case class Board (
   cells: Vector[Vector[Cell]],
   cellsStatuses: Vector[Vector[CellStatus]]
 ){
  val rows: Int = cells.length
  val cols: Int = if (cells.isEmpty) 0 else cells.head.length
  
  def countMines: Int = cells.flatten.count(cell => cell == Mine)

  def cellAt(row: Int, col: Int): Option[Cell] = {
    if (row >= 0 && row < rows && col >= 0 && col < cols)
      Some(cells(row)(col))
    else None
  }
  
  def cellStatusAt(row: Int, col: Int): Option[CellStatus] = {
    if (row >= 0 && row < rows && col >= 0 && col < cols)
      Some(cellsStatuses(row)(col))
    else None
  }
  def changeStatus(row: Int, col: Int)(f: CellStatus => CellStatus): Board =
    cellStatusAt(row, col) match
      case Some(_) =>
        val newCells =
          cellsStatuses.updated(
            row,
            cellsStatuses(row).updated(col, f(cellsStatuses(row)(col)))
          )
        copy(cellsStatuses = newCells)
      case None =>
        this
  
  
}