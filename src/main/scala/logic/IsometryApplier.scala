package logic

import logic.ExpandMode.{Expanding, NonExpanding}
import logic.OverlayMode.{Opaque, Transparent}
import model.{Bomb, CellType, Empty, Level}

object IsometryApplier {
  private def validBounds(level: Level, row: Int, col: Int): Boolean = {
    row >= 0 && col >= 0 && row < level.rows && col < level.cols
  }
  private def setCell(level: Level ,r: Int, c: Int, value: CellType): Level = {
    if !validBounds(level, r, c) then
      level
    else{
      val oldRow = level.cells(r)
      val updatedRow = oldRow.updated(c, value)
      level.copy(cells = level.cells.updated(r, updatedRow))
    }
  }
  private def setMines(mines: Vector[(Int, Int)], level: Level): Level = {
    mines.foldLeft(level) {
      case (lvl, (r, c)) => setCell(lvl, r, c, Bomb)
    }
  }
  private def setEmpties(empties: Vector[(Int, Int)], level: Level): Level = {
    empties.foldLeft(level) {
      case (lvl, (r, c)) => setCell(lvl, r, c, Empty)
    }
  }
  private def pictureRectangle(cells: Vector[(Int, Int)]): Rectangle = {
    if (cells.isEmpty) Rectangle(0, 0, -1, -1)
    else {
      val rowIndices = cells.map(_._1)
      val colIndices = cells.map(_._2)
      Rectangle(rowIndices.min, colIndices.min, rowIndices.max, colIndices.max)
    }
  }

  private def expand(mappedMinesRaw: Vector[(Int, Int)], mappedEmptiesRaw: Vector[(Int, Int)], level: Level, config: IsometryBaseConfiguration): (Level, Int, Int) = {
    val allCells = mappedMinesRaw ++ mappedEmptiesRaw
    if allCells.isEmpty then
      return (level, 0, 0)
    val (minRow, minCol, maxRow, maxCol) =
      allCells.foldLeft(
        (config.rectangle.startRow,
          config.rectangle.startCol,
          config.rectangle.endRow,
          config.rectangle.endCol)
      ) { case ((minR, minC, maxR, maxC), (r, c)) =>
        (
          Math.min(r, minR),
          Math.min(c, minC),
          Math.max(r, maxR),
          Math.max(c, maxC)
        )
      }
    val topLeft = Math.max(0, -minRow)
    val leftLeft = Math.max(0, - minCol)
    val bottomLeft = Math.max(0, maxRow - (level.rows - 1))
    val rightLeft = Math.max(0, maxCol - (level.cols - 1))
    val topAdded =
      (1 to topLeft).foldLeft(level)((lvl, _) => AddEmptyRowFirst(lvl))
    val leftAdded =
      (1 to leftLeft).foldLeft(topAdded)((lvl, _) => AddEmptyColumnFirst(lvl))
    val bottomAdded =
      (1 to bottomLeft).foldLeft(leftAdded)((lvl, _) => AddEmptyRowLast(lvl))
    ((1 to rightLeft).foldLeft(bottomAdded)((lvl, _) => AddEmptyColumnLast(lvl)), topLeft, leftLeft)

  }
  def apply(
           level: Level,
           config: IsometryBaseConfiguration,
           mappingFunction: (Int, Int) => (Int, Int)
           ): Level = {
    // broj redova
    val rows = level.rows
    // ako je nula onda je nivo prazan
    if rows == 0 then
      return level
    // broj kolona
    val cols = level.cols

    // uzimamo pravougaonik i normaliujemo ga (zamenimo koordinate ako je startRow, startCol > endRow, endCol )
    val rectangleRaw = config.rectangle.normalize()

    def validCoordinates(row: Int, col: Int): Boolean =
      row >= 0 && row < rows && col >= 0 && col < cols

    // ako koordinate nisu validne ne nastavljamo dalje i vracamo pocetni nivo
    if (!validCoordinates(rectangleRaw.startRow, rectangleRaw.startCol) || !validCoordinates(rectangleRaw.endRow, rectangleRaw.endCol))
      return level
    // dohvatamo sve mine u pravoguaoniku (njihove koordinate)
    val mines: Vector[(Int, Int)] =
      (for {
        r <- rectangleRaw.startRow to rectangleRaw.endRow
        c <- rectangleRaw.startCol to rectangleRaw.endCol
        if level.cells(r)(c) == Bomb
      } yield (r, c)).toVector

    // dohvatamo sva prazna polja u pravoguaoniku (njihove koordinate)
    val empties: Vector[(Int, Int)] =
      (for {
        r <- rectangleRaw.startRow to rectangleRaw.endRow
        c <- rectangleRaw.startCol to rectangleRaw.endCol
        if level.cells(r)(c) == Empty
      } yield (r, c)).toVector
    // mine mapirane potrebnom funkcijom (ali neobradjene)
    val mappedMinesRaw: Vector[(Int, Int)] = mines.map { case (r, c) => mappingFunction(r, c) }
    // prazna polja mapirana potrebnom funkcijom (ali neobradjena)
    val mappedEmptiesRaw: Vector[(Int, Int)] = empties.map { case (r, c) => mappingFunction(r, c) }
    // vratice nivo, malo sredjenije mapirane mine, malo sredjenija mapirana prazna polja, koliko treba da se pomeri u levo i koliko treba da se pomeri u desno
    val (levelToEdit, mappedMines, mappedEmpties, shiftTop, shiftLeft) =
      config.expand match
        case NonExpanding =>
          // ako je neprosirujuce, potrebno je da odbacimo sve ono sto je van opsega i vratimo 0 za pomeranja
          val minesFiltered = mappedMinesRaw.filter{case (r, c) => validBounds(level, r, c)}
          val emptiesFiltered = mappedEmptiesRaw.filter{case (r, c) => validBounds(level, r, c)}
          (level, minesFiltered, emptiesFiltered, 0 ,0)
        case Expanding =>
          // ako je prosirujuca, potrebno je da prosirimo level, a zatim mapirane mine i prazna polja pomerimo za onoliko za koliko se siftovalo sve
          val (newLevel, shiftTop, shiftLeft) = expand(mappedMinesRaw, mappedEmptiesRaw, level, config)
          val minesShifted = mappedMinesRaw.map { case (r, c) => (r + shiftTop, c + shiftLeft) }
          val emptiesShifted = mappedEmptiesRaw.map { case (r, c) => (r + shiftTop, c + shiftLeft) }
          (newLevel, minesShifted, emptiesShifted, shiftTop, shiftLeft)
    // siftujemo i pravougaonik ukoliko je to potrebno
    val rectangle = Rectangle(rectangleRaw.startRow + shiftTop, rectangleRaw.startCol + shiftLeft, rectangleRaw.endRow + shiftTop, rectangleRaw.endCol + shiftLeft )
    // potrebno je ocistiti polja pocetnog pravougaonika
    val levelClearedRectangle = ClearRectangle(rectangle.startRow, rectangle.startCol, rectangle.endRow, rectangle.endCol)(levelToEdit)
    config.overlay match {
      // ako je transparentno, onda samo postavimo sve nove mine
      case Transparent => setMines(mappedMines, levelClearedRectangle)
      case Opaque =>
        val pictureCells = mappedMines ++ mappedEmpties
        val levelClearedPicture =
          if (pictureCells.nonEmpty) then
            val picRect = pictureRectangle(pictureCells)
            ClearRectangle(picRect.startRow, picRect.startCol, picRect.endRow, picRect.endCol)(levelClearedRectangle)
          else
            levelClearedRectangle
        val levelWithMines = setMines(mappedMines, levelClearedPicture)
        setEmpties(mappedEmpties, levelWithMines)
    
    }






  }
}
