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
    val revealed = getAllRevealed // niz svih celija koje su otkrivene

    for ((r, c) <- revealed) { // prolazimo kroz sve

      val minesAround = neighborMines(r, c) // racunamo koliko ima mina okolo
      val flaggedAround = getFlaggedNeighborsCount(r, c) // racunamo koliko ima flegova okolo

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
    val f = flagsLeft // koliko je zastavica ostalo
    val c = countMines // koliko ukupno ima mina
    var defaultProbability: Double = flagsLeft.toDouble / getAllHiddenCount // defaultna verovatnoca nam je kolicnik preostalih zastavica i svih skrivenih polja
    if defaultProbability < 0 then // moze se desiti da je manja od nula ako korisnik ne igra dobro i iskoristi vise zastavica nego sto treba
      defaultProbability = 0.1 // TODO videti sta raditi u ovom slucaju, da li staviti na 0.1, da li na neku drugu vrednost ili zabraniti taj slucaj ogranicivsi broj mogucih zastavica na broj mina
    val hidden = getAllHidden // sva skrivena polja, za svako cemo racunati verovatnocu da je mina tako sto izracunamo verovatnocu na osnovu suseda koji su otvoreni i uzeti najvecu mogucu verovatnocu ili defaultn-u verovatnocu ako nema otvorenih suseda
    val probs = hidden.map {
      case (r, c) =>
        val revealedNeighbors = // ovde cuvamo sve otkrivene susede
          neighbors(r, c).filter { case (nr, nc) =>
            cellStatusAt(nr, nc).contains(Revealed)   
          }
        val prob: Double = // ovde cemo sacuvati verovatnocu za polje na osnovu tih otkrivenih suseda
          if (revealedNeighbors.isEmpty) { // ako nema otkrivenih suseda, onda default verovatnoca
            defaultProbability 
          }
          else {
            revealedNeighbors.map { // ako ima biramo najvecu mogucu verovatnocu
              case (nr, nc) =>
                val remainingMines = neighborMines(nr, nc) - getFlaggedNeighborsCount(nr, nc) // za suseda racunamo koliko mina mu nije otkriveno na osnovu flegovanih polja polazivsi od njegove pretpostavke gde su mine na osnovu flagova
                val hiddenNeighbors =
                  getHiddenNeighborsCount(nr, nc) // koliko ukupno sused ima skrivenih suseda
                remainingMines.toDouble / hiddenNeighbors max 1 // preostale mine kroz svi skriveni susedi daje verovatnocu naseg polja da je mina

            }.max // biramo max verovatnocu da bismo bili sto sigurniji
          }
        ((r, c), prob) // cuvamo polje i njegovu verovatnocu  koju smo dobili

    }
    val minProb = probs.map(_._2).minOption // biramo polja sa najmanjom verovatnocom
    minProb.flatMap { r => // ako ih ima vise biramo random
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