package logic

import logic.ExpandMode.{Expanding, NonExpanding}
import logic.OverlayMode.{Opaque, Transparent}
import model.{Bomb, CellType, Empty, Level}

object IsometryApplier {

  private def validBounds(level: Level, row: Int, col: Int): Boolean =
    row >= 0 && col >= 0 && row < level.rows && col < level.cols

  private def setCell(level: Level, r: Int, c: Int, value: CellType): Level =
    if !validBounds(level, r, c) then
      level
    else
      val oldRow = level.cells(r)
      val updatedRow = oldRow.updated(c, value)
      level.copy(cells = level.cells.updated(r, updatedRow))

  private def setMines(mines: Vector[(Int, Int)], level: Level): Level =
    mines.foldLeft(level) {
      case (lvl, (r, c)) => setCell(lvl, r, c, Bomb)
    }

  private def setEmpties(empties: Vector[(Int, Int)], level: Level): Level =
    empties.foldLeft(level) {
      case (lvl, (r, c)) => setCell(lvl, r, c, Empty)
    }

  private def pictureRectangle(cells: Vector[(Int, Int)]): Rectangle =
    if cells.isEmpty then
      Rectangle(0, 0, -1, -1)
    else
      val rowIndices = cells.map(_._1)
      val colIndices = cells.map(_._2)
      Rectangle(rowIndices.min, colIndices.min, rowIndices.max, colIndices.max)

  private def expand(
                      mappedMinesRaw: Vector[(Int, Int)],
                      mappedEmptiesRaw: Vector[(Int, Int)],
                      level: Level,
                      config: IsometryConfiguration
                    ): (Level, Int, Int) = {
    val allCells = mappedMinesRaw ++ mappedEmptiesRaw

    if allCells.isEmpty then
      return (level, 0, 0)

    val rect = config.rectangle.normalize()

    val (minRow, minCol, maxRow, maxCol) =
      allCells.foldLeft(
        (rect.startRow, rect.startCol, rect.endRow, rect.endCol)
      ) { case ((minR, minC, maxR, maxC), (r, c)) =>
        (
          Math.min(r, minR),
          Math.min(c, minC),
          Math.max(r, maxR),
          Math.max(c, maxC)
        )
      }

    val topToAdd = Math.max(0, -minRow)
    val leftToAdd = Math.max(0, -minCol)
    val bottomToAdd = Math.max(0, maxRow - (level.rows - 1))
    val rightToAdd = Math.max(0, maxCol - (level.cols - 1))

    val topAdded =
      (1 to topToAdd).foldLeft(level) { (lvl, _) => AddEmptyRowFirst(lvl) }

    val leftAdded =
      (1 to leftToAdd).foldLeft(topAdded) { (lvl, _) => AddEmptyColumnFirst(lvl) }

    val bottomAdded =
      (1 to bottomToAdd).foldLeft(leftAdded) { (lvl, _) => AddEmptyRowLast(lvl) }

    val fullyExpanded =
      (1 to rightToAdd).foldLeft(bottomAdded) { (lvl, _) => AddEmptyColumnLast(lvl) }

    (fullyExpanded, topToAdd, leftToAdd)
  }

  def apply(
             level: Level,
             config: IsometryConfiguration,
             mappingFunction: (Int, Int) => (Int, Int)
           ): Level = {
    val rows = level.rows
    if rows == 0 then
      return level

    val cols = level.cols
    val rectangleRaw = config.rectangle.normalize()

    def validCoordinates(row: Int, col: Int): Boolean =
      row >= 0 && row < rows && col >= 0 && col < cols

    if !validCoordinates(rectangleRaw.startRow, rectangleRaw.startCol) ||
      !validCoordinates(rectangleRaw.endRow, rectangleRaw.endCol) then
      return level

    val mines: Vector[(Int, Int)] =
      (for
        r <- rectangleRaw.startRow to rectangleRaw.endRow
        c <- rectangleRaw.startCol to rectangleRaw.endCol
        if level.cells(r)(c) == Bomb
      yield (r, c)).toVector

    val empties: Vector[(Int, Int)] =
      (for
        r <- rectangleRaw.startRow to rectangleRaw.endRow
        c <- rectangleRaw.startCol to rectangleRaw.endCol
        if level.cells(r)(c) == Empty
      yield (r, c)).toVector

    val mappedMinesRaw =
      mines.map { case (r, c) => mappingFunction(r, c) }

    val mappedEmptiesRaw =
      empties.map { case (r, c) => mappingFunction(r, c) }

    val (levelToEdit, mappedMines, mappedEmpties, shiftTop, shiftLeft) =
      config.expand match
        case NonExpanding =>
          val minesFiltered = mappedMinesRaw.filter { case (r, c) => validBounds(level, r, c) }
          val emptiesFiltered = mappedEmptiesRaw.filter { case (r, c) => validBounds(level, r, c) }
          (level, minesFiltered, emptiesFiltered, 0, 0)

        case Expanding =>
          val (newLevel, shiftTop, shiftLeft) =
            expand(mappedMinesRaw, mappedEmptiesRaw, level, config)

          val minesShifted =
            mappedMinesRaw.map { case (r, c) => (r + shiftTop, c + shiftLeft) }

          val emptiesShifted =
            mappedEmptiesRaw.map { case (r, c) => (r + shiftTop, c + shiftLeft) }

          (newLevel, minesShifted, emptiesShifted, shiftTop, shiftLeft)

    val rectangle = Rectangle(
      rectangleRaw.startRow + shiftTop,
      rectangleRaw.startCol + shiftLeft,
      rectangleRaw.endRow + shiftTop,
      rectangleRaw.endCol + shiftLeft
    )

    val levelClearedRectangle =
      ClearRectangle(
        rectangle.startRow,
        rectangle.startCol,
        rectangle.endRow,
        rectangle.endCol
      )(levelToEdit)

    config.overlay match
      case Transparent =>
        setMines(mappedMines, levelClearedRectangle)

      case Opaque =>
        val pictureCells = mappedMines ++ mappedEmpties

        val levelClearedPicture =
          if pictureCells.nonEmpty then
            val picRect = pictureRectangle(pictureCells)
            ClearRectangle(
              picRect.startRow,
              picRect.startCol,
              picRect.endRow,
              picRect.endCol
            )(levelClearedRectangle)
          else
            levelClearedRectangle

        val levelWithMines = setMines(mappedMines, levelClearedPicture)
        setEmpties(mappedEmpties, levelWithMines)
  }
}