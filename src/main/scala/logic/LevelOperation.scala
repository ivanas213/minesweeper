package logic

import model.{Bomb, CellType, Empty, Level}

sealed trait LevelOperation extends (Level => Level)

case class AddEmptyRowFirst() extends LevelOperation{
  override def apply(level: Level): Level = {
    if level.cells.isEmpty then 
      level
    else {
      val newRow = Vector.fill(level.cols)(Empty)
      level.copy(cells = newRow +: level.cells)
    }
  }
}

case object AddEmptyRowLast extends LevelOperation{
  override def apply(level: Level): Level = {
    if level.cells.isEmpty then 
      level
    else {
      val newRow = Vector.fill(level.cols)(Empty)
      level.copy(cells = level.cells :+ newRow)
    }
  }
}

case object AddEmptyColumnFirst extends LevelOperation{
  override def apply(level: Level): Level = {
    if level.cells.isEmpty then 
      level
    else {
      val newCells = level.cells.map(row => Empty +: row)
      level.copy(cells = newCells)
    }
  }
}

case object AddEmptyColumnLast extends LevelOperation{
  override def apply(level: Level): Level = {
    if level.cells.isEmpty then 
      level
    else {
      val newCells = level.cells.map(row => row :+ Empty)
      level.copy(cells = newCells)
    }
  }
}

case class ToggleCell(row: Int, col: Int) extends  LevelOperation{
  override def apply(level: Level): Level = {
    if (
      row < 0 || col < 0 || row >= level.rows || col >= level.cols
    ) level
    else{
      val oldRow = level.cells(row)
      val newCell = oldRow(col) match {
        case Bomb => Empty
        case Empty => Bomb
      }
      val newRow = oldRow.updated(col, newCell)
      val newCells = level.cells.updated(row, newRow)
      level.copy(cells = newCells)
    }
  }  
}

case object RemoveFirstRow extends LevelOperation {

  override def apply(level: Level): Level = {
    if level.cells.isEmpty then
      level
    else
      level.copy(cells = level.cells.drop(1))
  }

}

case object RemoveLastRow extends LevelOperation {

  override def apply(level: Level): Level = {
    if level.cells.isEmpty then
      level
    else
      level.copy(cells = level.cells.dropRight(1))
  }

}
case object RemoveFirstColumn extends LevelOperation{
  
  override def apply(level: Level): Level = {
    if level.cells.isEmpty then 
      level
    else
      val newCells = level.cells.map(row => row.drop(1))
      level.copy(cells = newCells)
  }
 
}

case object RemoveLastColumn extends LevelOperation{
  
  override def apply(level: Level): Level = {
    if level.cells.isEmpty then 
      level
    else
      val newCells = level.cells.map(row => row.dropRight(1))
      level.copy(cells = newCells)
  }
 
}

case class ClearRectangle(startRow: Int, startCol: Int, endRow: Int, endCol: Int) extends LevelOperation{
  
  override def apply(level: Level): Level = {
    if level.cells.isEmpty || startRow >= endRow || startCol >= endCol || startRow < 0 || startCol < 0 then  // TODO mozda bolje da baca gresku
      level
    else
      val newCells: Vector[Vector[CellType]] = level.cells.zipWithIndex.map {
        case (row, i)  => 
          if (i >= startRow && i <= endRow) {
            row.zipWithIndex.map{
              case (cell, j) => 
                if j >= startCol && j <= endCol then
                  Empty
                else 
                  cell
              case _ => throw new Exception("Unknown exception")
            }
          }
          else throw new Exception("Unknown exception")

        case _ => throw new Exception("Unknown exception")
      }
      level.copy(cells = newCells)
  }
 
}

// TODO videti mozda neke granicne slucajeve npr kada ima samo jedan red i uklanja se on i isto i za kolonu
// TODO da bude lepse ovo za Exception