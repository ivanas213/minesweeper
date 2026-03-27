package model

import scala.util.Random

case class Board (
   cells: Vector[Vector[Cell]],
   cellsStatuses: Vector[Vector[CellStatus]],
   difficulty: Difficulty
 ){
  val rows: Int = cells.length
  val cols: Int = if (cells.isEmpty) 0 else cells.head.length
  
  def countMines: Int = cells.flatten.count(cell => cell == Mine)

  def countNonZeroNumbers: Int =
    cells.flatten.count {
      case Number(value) if value > 0 => true
      case _ => false
    }
    
  def cellAt(row: Int, col: Int): Option[Cell] = {
    if (row >= 0 && row < rows && col >= 0 && col < cols)
      Some(cells(row)(col))
    else None
  }

  def neighbors(row: Int, col: Int): Seq[(Int, Int)] =
    for {
      dx <- -1 to 1
      dy <- -1 to 1
      if !(dx == 0 && dy == 0)
      r = row + dx
      c = col + dy
      if r >= 0 && r < rows
      if c >= 0 && c < cols
    } yield (r, c)

  def neighborMines(row: Int, col: Int): Int =
    neighbors(row, col)
      .flatMap { case (r, c) => cellAt(r, c) }
      .count(_ == Mine)

  private def getFlaggedNeighborsCount(row: Int, col: Int): Int = {
    val cellNeighbors = neighbors(row, col)
    val flagged = for {
      neighbor <- cellNeighbors
      if cellStatusAt(neighbor._1, neighbor._2).get == Flagged
    } yield (neighbor._1, neighbor._2)
    flagged.length
  }

  private def getNonFlaggedNeighbor(row: Int, col: Int): Option[(Int, Int)] = {
    val cellNeighbors = neighbors(row, col)
    cellNeighbors.find { neighbor => {
      val cellStatus = cellStatusAt(neighbor._1, neighbor._2).get
      cellStatus == Hidden
    }
    }
  }
  private def getAllRevealed: Seq[(Int, Int)] = {
    for {
      r <- cells.indices
      c <- cells(r).indices
      if cellStatusAt(r, c).get == Revealed
    } yield (r, c)
  }

  def getSafeCell: Option[(Int, Int)] = {
    val revealed = getAllRevealed 

    for ((r, c) <- revealed) { 

      val minesAround = neighborMines(r, c) 
      val flaggedAround = getFlaggedNeighborsCount(r, c) 

      if (minesAround == flaggedAround) {

        val candidate = getNonFlaggedNeighbor(r, c)

        if (candidate.nonEmpty) {
          return candidate
        }
      }
    }

    None
  }

  def flagsLeft: Int = {
    val flagged = for {
      r <- cells.indices
      c <- cells(r).indices
      if cellStatusAt(r, c).get == Flagged
    } yield (r, c)
    
    val mines = for {
      r <- cells.indices
      c <- cells(r).indices
      if cellAt(r, c).get == Mine
    } yield (r, c)
    
    mines.size - flagged.size
  }

  private def getAllHiddenCount: Int = {
    getAllHidden.length
  }

  private def getAllHidden: Seq[(Int, Int)] = {
    for {
      r <- cells.indices
      c <- cells(r).indices
      if cellStatusAt(r, c).get == Hidden
    } yield (r, c)
  }

  private def getHiddenNeighborsCount(row: Int, col: Int): Int = {
    val cellNeighbors = neighbors(row, col)
    val hidden = for {
      neighbor <- cellNeighbors
      if (cellStatusAt(neighbor._1, neighbor._2).contains(Hidden))
    }
    yield (neighbor._1, neighbor._2)
    hidden.length
  }
  
  def getMaybeSafeCell: Option[(Int, Int)] = {
    val f = flagsLeft 
    val c = countMines 
    var defaultProbability: Double = flagsLeft.toDouble / getAllHiddenCount 
    if defaultProbability < 0 then 
      defaultProbability = 0.1 
    val hidden = getAllHidden 
    val probs = hidden.map {
      case (r, c) =>
        val revealedNeighbors = 
          neighbors(r, c).filter { case (nr, nc) =>
            cellStatusAt(nr, nc).contains(Revealed)   
          }
        val prob: Double = 
          if (revealedNeighbors.isEmpty) { 
            defaultProbability 
          }
          else {
            revealedNeighbors.map { 
              case (nr, nc) =>
                val remainingMines = neighborMines(nr, nc) - getFlaggedNeighborsCount(nr, nc) 
                val hiddenNeighbors =
                  getHiddenNeighborsCount(nr, nc) 
                remainingMines.toDouble / hiddenNeighbors max 1 

            }.max 
          }
        ((r, c), prob) 

    }
    val minProb = probs.map(_._2).minOption 
    minProb.flatMap { r => 
      val safest = probs.filter(_._2 == r).map(_._1)
      Random.shuffle(safest).headOption
    }


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