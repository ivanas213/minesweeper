package model

case class Board (
                 rows: Int,
                 cols: Int,
                 cells: Vector[Vector[Cell]],
                 cellsStatuses: Vector[Vector[CellStatus]]
                 )