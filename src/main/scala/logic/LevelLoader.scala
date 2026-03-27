package logic
//
import model.{Beginner, Board, Bomb, Cell, CellStatus, CellType, Difficulty, Empty, Expert, Hidden, Intermediate, Level, LevelParameters, Mine}

import java.io.{File, PrintWriter}
import scala.math.Numeric.Implicits.*
import scala.io.Source
import scala.util.Random

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
  
  def loadLevel(path: String): Level = {
    val lines = Source.fromFile(path).getLines().toVector
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
  def getRandomLevel(levels: Vector[LevelParameters]): LevelParameters = levels(Random.nextInt(levels.length))

  def getDifficulty(rows: Int, cols: Int): Difficulty = {
    if rows >= Beginner.minRows && rows <= Beginner.maxRows && cols >= Beginner.minCols && cols <= Beginner.maxCols then
      Beginner
    else if rows >= Intermediate.minRows && rows <= Intermediate.maxRows && cols >= Intermediate.minCols && cols <= Intermediate.maxCols  then
      Intermediate
    else if rows >= Expert.minRows && rows <= Expert.maxRows && cols >= Expert.minCols && cols <= Expert.maxCols then
      Expert
    else
      throw Exception("Unknown difficulty " + rows + cols)
  }
  def getLevelsByDifficulty(difficulty: Difficulty): Vector[LevelParameters] = difficulty.levels
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
