package logic
//
import model.{Board, Cell, CellStatus, Hidden, Mine}
import utilities.{BeginnerConstants, DifficultyConstants, ExpertConstants, IntermediateConstants}

import scala.math.Numeric.Implicits.*
import scala.io.Source

object LevelLoader{

  def loadLevel(path: String): Board = {
    // treba proveriti npr da li je pravougaona
    val lines = Source.fromFile(path).getLines().toVector
    if (lines.isEmpty)
      throw new Exception("Level file can't be empty")
    val rows = lines.length
    val cols = lines.head.length
    if (!lines.forall(_.length == cols))
      throw new IllegalArgumentException("Level must be rectangular")
    val mines: Vector[Vector[Boolean]] =
      lines.map { line =>
        line.map {
          case '#' => true
          case '-' => false
          case other =>
            throw new Exception(s"Invalid character: $other")
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
    val difficulty =  getDifficulty(rows, cols)
    Board(cells, statuses, difficulty)
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

  def getDifficulty(rows: Int, cols: Int): Difficulty = {
    if rows >= BeginnerConstants.minRows && rows <= BeginnerConstants.maxRows then
      Beginner
    else if rows >= IntermediateConstants.minRows && rows <= IntermediateConstants.maxRows then
      Intermediate
    else if rows >= ExpertConstants.minRows && rows <= ExpertConstants.maxRows then
      Expert
    else
      throw Exception("Unknown difficulty " + rows)
  }

  def addRowFirst(path: String, newLevelName: String): Unit = {

  }
  // znaci kad se udje na izbor nivoa od kog se pravi novi nivo potrebna nam je neka metoda koja ce vratiti tekstualnu 
}
