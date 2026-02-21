package model

import logic.Difficulty


case class Level(
                  cells: Vector[Vector[CellType]],
                  difficulty: Difficulty
                )
{
  val rows: Int = cells.length
  val cols: Int = if (cells.isEmpty) 0 else cells.head.length
}

