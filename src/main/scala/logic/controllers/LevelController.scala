package logic.controllers

import logic.*
import model.{Bomb, Empty, Level}
import ui.view.{EmptyLevelCellView, LevelCellView, MineLevelCellView}

class LevelController(path: String) {

  private var level: Level = LevelLoader.loadLevel(path)
  def getLevel: Level = level
  def rows: Int = level.rows
  def cols: Int = level.cols
  private var savedIsometries: Map[String, Isometry] = Map.empty
  private var currentIsometry: Option[Isometry] = None
  private var currentIsometryNames: List[String] = List.empty
  def isLevelValid: Boolean =
    LevelLoader.isValid(level.rows, level.cols, level.mines, level.difficulty)
  def saveLevel(name: String): Unit = LevelLoader.saveLevel(level, name)
  def addEmptyRowFirst(): Unit = level = AddEmptyRowFirst(level)
  def addEmptyRowLast(): Unit = level = AddEmptyRowLast(level)
  def addEmptyColumnFirst(): Unit = level = AddEmptyColumnFirst(level)
  def addEmptyColumnLast(): Unit = level = AddEmptyColumnLast(level)
  def removeFirstRow(): Unit = level = RemoveFirstRow(level)
  def removeLastRow(): Unit = level = RemoveLastRow(level)
  def removeFirstColumn(): Unit = level = RemoveFirstColumn(level)
  def removeLastColumn(): Unit = level = RemoveLastColumn(level)
  def toggleCell(row: Int, col: Int): Unit = level = ToggleCell(row, col)(level)
  def clearRectangle(startRow: Int, startCol: Int, endRow: Int, endCol: Int): Unit = level = ClearRectangle(startRow, startCol, endRow, endCol)(level)
  def getLevelCellView(row:Int, col: Int): LevelCellView = {
    val cell = level.cellAt(row, col)
    cell match
      case Some(Bomb) => MineLevelCellView()
      case Some(Empty) => EmptyLevelCellView()
      case _ => EmptyLevelCellView()
  }
  def onSave(level: Level, name: String): Unit ={
    throw new NotImplementedError()
  }
  def applyRotation(startRow: Int, startCol: Int, endRow: Int, endCol: Int, expandMode: ExpandMode, overlay: OverlayMode, pivotRow: Int, pivotCol: Int, rotationDirection: RotationDirection, quasiInverse: Boolean): Unit ={
    val rectangle = Rectangle(startRow, startCol, endRow, endCol)
    val config = IsometryConfiguration(rectangle, expandMode, overlay)
    val isometry =
      if !quasiInverse then Rotation90(rotationDirection, (pivotRow, pivotCol))
      else Rotation90(rotationDirection, (pivotRow, pivotCol)).quasiInverse
    level = runIsommetry(isometry, level, config)
      
  }
  private def runIsommetry(isometry: Isometry, level: Level, config: IsometryConfiguration): Level = isometry.run(level, config) match
    case Right(newLevel) => newLevel
    case Left(_) => level
  def addRotation(pivotRow: Int, pivotCol: Int, rotationDirection: RotationDirection): Unit ={
    addToCurrentIsometry(logic.Rotation90(rotationDirection, (pivotRow, pivotCol)))
    currentIsometryNames = currentIsometryNames :+ s"Ротација за 90 степени око тачке (${pivotRow}, ${pivotCol})"
  }
  def applyReflection(startRow: Int, startCol: Int, endRow: Int, endCol: Int, row: Option[Int], col: Option[Int], diagonal1: Option[(Int, Int)], diagonal2: Option[(Int, Int)], expandMode: ExpandMode, overlayMode:  OverlayMode, quasiInverse: Boolean): Unit = {
    val reflectionAxis = if row.isDefined then ReflectionAxis.Horizontal(row.get)
    else if col.isDefined then ReflectionAxis.Vertical(col.get)
    else if diagonal1.isDefined then ReflectionAxis.DiagonalMain(diagonal1.get._1, diagonal1.get._2)
    else if diagonal2.isDefined then ReflectionAxis.DiagonalSecondary(diagonal2.get._1, diagonal2.get._2)
    else return
    val rectangle = Rectangle(startRow, startCol, endRow, endCol)
    val config = IsometryConfiguration(rectangle, expandMode, overlayMode)
    val isometry = if !quasiInverse then Reflection(reflectionAxis)
    else Reflection(reflectionAxis).quasiInverse
    level = runIsommetry(isometry, level, config)
  }
  def addReflection(row: Option[Int], col: Option[Int], diagonal1: Option[(Int, Int)], diagonal2: Option[(Int, Int)]): Unit = {
    val reflectionAxis = if row.isDefined then
      currentIsometryNames = currentIsometryNames :+ s"Осна рефлексија око реда ${row.get}"
      ReflectionAxis.Horizontal(row.get)
    else if col.isDefined then
      currentIsometryNames = currentIsometryNames :+ s"Осна рефлексија око колоне ${col.get}"
      ReflectionAxis.Vertical(col.get)
    else if diagonal1.isDefined then
      currentIsometryNames = currentIsometryNames :+ s"Осна рефлексија око главне дијагонале која пролази кроз тачку (${diagonal1.get._1}, ${diagonal1.get._2})"
      ReflectionAxis.DiagonalMain(diagonal1.get._1, diagonal1.get._2)
    else if diagonal2.isDefined then
      currentIsometryNames = currentIsometryNames :+ s"Осна рефлексија око споредне дијагонале која пролази кроз тачку (${diagonal2.get._1}, ${diagonal2.get._2})"
      ReflectionAxis.DiagonalSecondary(diagonal2.get._1, diagonal2.get._2)

    else throw new NotImplementedError()
    addToCurrentIsometry(logic.Reflection(reflectionAxis))
  }

  def applyCentralSymmetry(startRow: Int, startCol: Int, endRow: Int, endCol: Int, expandMode: ExpandMode, overlay: OverlayMode, pivotRow: Int, pivotCol: Int, quasiInverse: Boolean): Unit = {
    val rectangle = Rectangle(startRow, startCol, endRow, endCol)
    val config = IsometryConfiguration(rectangle, expandMode, overlay)
    val isometry = if !quasiInverse then CentralSymmetry(pivotRow, pivotCol)
    else CentralSymmetry(pivotRow, pivotCol).quasiInverse
    level = runIsommetry(isometry, level, config)
  }

  def getSavedIsometries: List[Isometry] =
    savedIsometries.values.toList
  def getSavedIsometryNames: List[String] =
    savedIsometries.keys.toList
  def addCentralSymmetry(pivotRow: Int, pivotCol: Int): Unit = {
    addToCurrentIsometry(CentralSymmetry((pivotRow, pivotCol)))
    currentIsometryNames = currentIsometryNames :+ s"Централна симетрија око тачке (${pivotRow}, ${pivotCol})"
  }
  def applyTranslation(startRow: Int, startCol: Int, endRow: Int, endCol: Int, newStartRow: Int, newStartCol: Int, expandMode: ExpandMode, overlay: OverlayMode, quasiInverse: Boolean): Unit = {
    val rectangle = Rectangle(startRow, startCol, endRow, endCol).normalize()
    val config = IsometryConfiguration(rectangle, expandMode, overlay)
    val shiftHorizontal = (newStartCol - rectangle.startCol) / 2
    val shiftVertical = (newStartRow - rectangle.startRow) / 2
    val isometry = if !quasiInverse then Translation(shiftHorizontal, shiftVertical)
    else Translation(shiftHorizontal, shiftVertical).quasiInverse
    level = runIsommetry(isometry, level, config)


  }
  def addTranslation(shiftRows: Int, shiftCols: Int): Unit = {
    addToCurrentIsometry(Translation(shiftRows, shiftCols))
    currentIsometryNames = currentIsometryNames :+ s"Транслација за померај (${shiftRows}, ${shiftCols})"

  }


  private def isometryList(isometry: Isometry): List[Isometry] =
    isometry match
      case CompositeIsometry(steps) => steps
      case other => List(other)


  private def addToCurrentIsometry(newIsometry: Isometry): Unit =
    currentIsometry =
      currentIsometry match
        case None =>
          Some(newIsometry)
        case Some(isometry) =>
          Some(isometry.chain(newIsometry))
  def resetIsometry(): Unit= {
    currentIsometry = None
    currentIsometryNames= List.empty
  }
  def getIsometryStepNames: List[String] = currentIsometryNames

  def saveCurrentIsometry(name: String): Unit = {
    currentIsometry match
      case Some(isometry) =>
        savedIsometries += (name -> isometry)
      case None =>
        ()
  }

  def applySavedIsometry(
                          name: String,
                          startRow: Int,
                          startCol: Int,
                          endRow: Int,
                          endCol: Int,
                          expandMode: ExpandMode,
                          overlayMode: OverlayMode,
                          quasiInverse: Boolean
                        ): Unit = {
    savedIsometries.get(name) match
      case Some(isometry) =>
        val rectangle = Rectangle(startRow, startCol, endRow, endCol)
        val config = IsometryConfiguration(rectangle, expandMode, overlayMode)
        val finalIsometry = if !quasiInverse then isometry
        else isometry.quasiInverse
        level = runIsommetry(finalIsometry, level, config)

      case None =>
        ()
  }

}
