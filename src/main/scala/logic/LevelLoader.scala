package logic
//
import model.{Board, Cell, CellStatus, Hidden, Mine}
import utilities.DifficultyConstants
import scala.math.Numeric.Implicits.*

import scala.io.Source

object LevelLoader{

  def loadLevel(path: String): Board = {
    // treba proveriti npr da li je pravougaona
    val lines = Source.fromFile(path).getLines().toVector
    val rows = lines.length
    val cols = lines.head.length
    val mines: Vector[Vector[Boolean]] =
      lines.map { line =>
        line.map {
          case '#' => true
          case '-' => false
          case other =>
            throw new IllegalArgumentException(s"Invalid character: $other")
        }.toVector
      }
    def countNeighborMines(r: Int, c:Int): Int =
      (for{
        dx <- -1 to 1
        dy <- -1 to 1
        if !(dx == 0 && dy ==0)
        x = r + dx
        y = c + dy
        if x >= 0 && x < rows && y >= 0 && y < cols
        if mines(x)(y)
      } yield 1).sum

    val cells: Vector[Vector[Cell]] = Vector.tabulate(rows, cols) { (r, c) =>
      if (mines(r)(c)) Mine
      else model.Number(countNeighborMines(r, c))
    }
    val statuses: Vector[Vector[CellStatus]] = Vector.fill(cells.length, cells.head.length)(Hidden)


    Board(cells, statuses)
  }

  def isValid(rows: Int, cols: Int, mines: Int, difficulty: DifficultyConstants): Boolean = {
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
