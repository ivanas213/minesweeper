package model

import logic.Difficulty


case class Level(
                  cells: Vector[Vector[CellType]],
                  difficulty: Difficulty
                )
{
  val rows: Int = cells.length
  val cols: Int = if (cells.isEmpty) 0 else cells.head.length
  def mines: Int = cells.flatten.count(cell => cell == Bomb)

  def cellAt(row: Int, col: Int): Option[CellType] = {
    if (row >= 0 && row < rows && col >= 0 && col < cols)
      Some(cells(row)(col))
    else None
  }
}

