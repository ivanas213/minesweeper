package logic
//
import model.{Board, Bomb, Cell, CellStatus, CellType, Empty, Hidden, Level, Mine}

import java.io.{File, PrintWriter}
import scala.math.Numeric.Implicits.*
import scala.io.Source

object LevelLoader{

  def loadGame(path: String): Board = {
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
  
  def loadLevel(path: String): Level = { // TODO mozda videti nesto da moze i prazan nivo
    val lines = Source.fromFile(path).getLines().toVector // TODO videti za ovo sto nije closed i ovde i na drugom mestu gde ima
    if (lines.isEmpty)
      throw new Exception("Level file can't be empty")
    val rows = lines.length
    val cols = lines.head.length
    if (!lines.forall(_.length == cols))
      throw new IllegalArgumentException("Level must be rectangular")
    val cells: Vector[Vector[CellType]] =
      lines.map { line =>
        line.map {
          case '#' => Bomb
          case '-' => Empty
          case other =>
            throw new Exception(s"Invalid character: $other")
        }.toVector
      }
    val difficulty =  getDifficulty(rows, cols)
    Level(cells, difficulty)
  }

  def isValid(rows: Int, cols: Int, mines: Int, difficulty: Difficulty): Boolean = {
    val cells = rows * cols
    val mineRatio = mines.toDouble / cells * 100
    rows >= difficulty.minRows
      && rows <= difficulty.maxRows
      && cols >= difficulty.minCols
      && cols <= difficulty.maxCols
      && mineRatio >= difficulty.minMineRatio
      && mineRatio <= difficulty.maxMineRatio
    }

  def getDifficulty(rows: Int, cols: Int): Difficulty = {
    if rows >= Beginner.minRows && rows <= Beginner.maxRows then
      Beginner
    else if rows >= Intermediate.minRows && rows <= Intermediate.maxRows then
      Intermediate
    else if rows >= Expert.minRows && rows <= Expert.maxRows then
      Expert
    else
      throw Exception("Unknown difficulty " + rows)
  }
  def saveLevel(level: Level, name: String): Unit = {
    def folder = level.difficulty match
      case Beginner => "beginner"
      case Intermediate => "intermediate"
      case Expert => "expert"
    val path = new File(s"levels/${folder}")  
    if !path.exists() then
      path.mkdirs()
    val file = new File(path, s"${name}.txt")
    val writer = new PrintWriter(file)
    try
      val content = level.cells.map{
        row => 
          row.map{
            case Bomb => "#"
            case Empty => "-"
          }.mkString
      }.mkString("\n")
      writer.write(content)
    finally 
      writer.close()
  }

 
}
