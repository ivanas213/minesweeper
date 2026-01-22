package logic

import model.{Board, Cell, Difficulty, Empty, Hidden, Mine}

import scala.io.Source

object LevelLoader{

  def loadLevel(path: String): Board = {
    
    val lines = Source.fromFile(path).getLines().toVector
    
    val cols = lines.head.length
    val cells =
      lines.map { line =>
        line.map {
          case '#' => Cell(Mine, Hidden)
          case '-' => Cell(Empty, Hidden)
          case other =>
            throw new IllegalArgumentException(s"Invalid character: $other")
        }.toVector
      }

    Board(cells)
  }

  def isValid(rows: Int, cols: Int, mines: Int, difficulty: Difficulty): Boolean = {
    val cells = rows * cols
    val mineRatio = mines.toDouble / cells * 100
    rows >= difficulty.minRows
      && rows <= difficulty.maxRows
      && cols >= difficulty.minColumns
      && cols <= difficulty.maxColumns
      && mineRatio >= difficulty.minMineRatio
      && mineRatio <= difficulty.maxMineRatio
  }
}
