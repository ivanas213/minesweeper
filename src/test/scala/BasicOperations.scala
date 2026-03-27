import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.funsuite.AnyFunSuite
import logic.controllers.LevelController
import model.{Bomb, Empty, Flagged, Hidden, Lost, Number, Playing, Revealed, Won}

import java.io.{File, PrintWriter}
class BasicOperations extends AnyFunSuite{
  test("add row first") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.addEmptyRowFirst()
    val level = levelController.getLevel
    assert(level.rows == 6)
    assert(level.cellAt(0, 0).get == Empty)
    assert(level.cellAt(1, 0).get == Bomb)

  }
  test("add row last") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.addEmptyRowLast()
    val level = levelController.getLevel
    assert(level.rows == 6)
    assert(level.cellAt(5, 0).get == Empty)

  }
  test("add column first") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.addEmptyColumnFirst()
    val level = levelController.getLevel
    assert(level.cols == 6)
    assert(level.cellAt(0, 0).get == Empty)
    assert(level.cellAt(0, 1).get == Bomb)

  }
  test("add column last") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#---#
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.addEmptyColumnLast()
    val level = levelController.getLevel
    assert(level.cols == 6)
    assert(level.cellAt(0, 5).get == Empty)
  }
  test("remove first row") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.removeFirstRow()
    val level = levelController.getLevel
    assert(level.rows == 4)
    assert(level.cellAt(0, 0).get == Empty)
    assert(level.cellAt(0, 1).get == Bomb)

  }
  test("remove last row") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |--#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.removeLastRow()
    val level = levelController.getLevel
    assert(level.rows == 4)
    assert(level.cellAt(3, 0).get == Empty)

  }
  test("remove first column") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.removeFirstColumn()
    val level = levelController.getLevel
    assert(level.cols == 4)
    assert(level.cellAt(0, 0).get == Empty)

  }
  test("remove last column") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#---#
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.removeLastColumn()
    val level = levelController.getLevel
    assert(level.cols == 4)
    assert(level.cellAt(0, 3).get == Empty)
  }
  test("change cell type from empty to mine") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#---#
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.toggleCell(0, 1)
    val level = levelController.getLevel
    assert(level.cellAt(0, 1).get == Bomb)
  }
  test("change cell type from mine to empty") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#---#
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.toggleCell(0, 0)
    val level = levelController.getLevel
    assert(level.cellAt(0, 0).get == Empty)
  }
  test("clear rectangle") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#---#
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.clearRectangle(startRow = 3, startCol = 0, endRow = 4, endCol = 1)
    val level = levelController.getLevel
    assert(level.cellAt(3, 0).get == Empty)
    assert(level.cellAt(3, 1).get == Empty)
    assert(level.cellAt(4, 0).get == Empty)
    assert(level.cellAt(4, 1).get == Empty)
  }
}
