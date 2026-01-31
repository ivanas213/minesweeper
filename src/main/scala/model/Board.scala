package model

case class Board (
   cells: Vector[Vector[Cell]],
   cellsStatuses: Vector[Vector[CellStatus]]
 ){
   val rows: Int = cells.length
   val cols: Int = if (cells.isEmpty) 0 else cells.head.length
}