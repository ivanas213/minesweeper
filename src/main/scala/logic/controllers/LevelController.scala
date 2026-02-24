package logic.controllers

import logic.*
import model.{Bomb, Empty, Level}
import ui.view.{EmptyLevelCellView, LevelCellView, MineLevelCellView}
import utilities.enumeration.{BeginnerConstants, DifficultyConstants, IntermediateConstants}

class LevelController(path: String) {

  private var level: Level = LevelLoader.loadLevel(path)
  def rows: Int = level.rows
  def cols: Int = level.cols
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
}
