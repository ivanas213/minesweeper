import logic.ExpandMode.{Expanding, NonExpanding}
import logic.OverlayMode.{Opaque, Transparent}
import logic.RotationDirection.{CCW, CW}
import org.scalatest.funsuite.AnyFunSuite
import logic.controllers.LevelController
import model.{Bomb, Empty}

import java.io.{File, PrintWriter}

class Isometry extends AnyFunSuite {
  test("basic rotation 90 CW"){
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
    levelController.applyRotation(startRow = 0, startCol = 0, endRow = 1, endCol = 1, expandMode = NonExpanding, overlay = Opaque, pivotRow = 2, pivotCol = 2, rotationDirection = CW, quasiInverse = false)
    val level = levelController.getLevel
    assert(level.cellAt(1, 3).get == Bomb)
    assert(level.cellAt(0, 4).get == Bomb)
    assert(level.cellAt(0, 0).get == Empty)
    assert(level.cellAt(1, 1).get == Empty)

  }
  test("basic rotation 90 CCW") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#-#--
        |-#---
        |-----
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyRotation(startRow = 0, startCol = 1, endRow = 1, endCol = 2, expandMode = NonExpanding, overlay = Opaque, pivotRow = 2, pivotCol = 3, rotationDirection = CCW, quasiInverse = false)
    val level = levelController.getLevel
    assert(level.cellAt(3, 1).get == Bomb)
    assert(level.cellAt(4, 2).get == Bomb)
    assert(level.cellAt(0, 2).get == Empty)
    assert(level.cellAt(1, 1).get == Empty)

  }
  test(" rotation 90 CW quasi inverse") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#-#--
        |-#---
        |-----
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyRotation(startRow = 0, startCol = 1, endRow = 1, endCol = 2, expandMode = NonExpanding, overlay = Opaque, pivotRow = 2, pivotCol = 3, rotationDirection = CW, quasiInverse = true)
    val level = levelController.getLevel
    assert(level.cellAt(3, 1).get == Bomb)
    assert(level.cellAt(4, 2).get == Bomb)
    assert(level.cellAt(0, 2).get == Empty)
    assert(level.cellAt(1, 1).get == Empty)

  }
  test("rotation 90 CW expandable") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyRotation(startRow = 0, startCol = 1, endRow = 1, endCol = 2, expandMode = Expanding, overlay = Opaque, pivotRow = 0, pivotCol = 3, rotationDirection = CW, quasiInverse = false)
    val level = levelController.getLevel
    assert(level.rows == 7)
    assert(level.cellAt(0, 2).get == Bomb)
    assert(level.cellAt(1, 3).get == Bomb)


  }
  test("rotation 90 CW non expandable") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyRotation(startRow = 0, startCol = 1, endRow = 1, endCol = 2, expandMode = NonExpanding, overlay = Opaque, pivotRow = 0, pivotCol = 3, rotationDirection = CW, quasiInverse = false)
    val level = levelController.getLevel
    assert(level.rows == 5)
    assert(level.cellAt(0, 2).get == Empty)
    assert(level.cellAt(1, 1).get == Empty)


  }
  test("rotation 90 CW transparent") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    val oldLevel = levelController.getLevel
    levelController.applyRotation(startRow = 3, startCol = 3, endRow = 4, endCol = 4, expandMode = NonExpanding, overlay = Transparent, pivotRow = 2, pivotCol = 2, rotationDirection = CW, quasiInverse = false)
    val newLevel = levelController.getLevel
    assert(newLevel == oldLevel)


  }
  test("rotation 90 CW opaque") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyRotation(startRow = 3, startCol = 3, endRow = 4, endCol = 4, expandMode = NonExpanding, overlay = Opaque, pivotRow = 2, pivotCol = 2, rotationDirection = CW, quasiInverse = false)
    val level= levelController.getLevel
    assert(level.cellAt(3, 1).get == Empty)
    assert(level.cellAt(4, 1).get == Empty)

  }
  test("horizontal axis reflexion expandable") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyReflection(
      startRow = 0, startCol = 0, endRow = 2, endCol = 4, row = Option(3), col = None, diagonal1 = None, diagonal2 = None, expandMode = Expanding, overlayMode = Transparent, quasiInverse = false
    )
    val level= levelController.getLevel
    assert(level.rows == 7)
    assert(level.cellAt(4, 3).get == Bomb)
    assert(level.cellAt(5, 1).get == Bomb)
    assert(level.cellAt(6, 2).get == Bomb)

  }
  test("horizontal axis reflexion expandable quasi inverse") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyReflection(
      startRow = 0, startCol = 0, endRow = 2, endCol = 4, row = Option(3), col = None, diagonal1 = None, diagonal2 = None, expandMode = Expanding, overlayMode = Transparent, quasiInverse = true
    )
    val level= levelController.getLevel
    assert(level.rows == 7)
    assert(level.cellAt(4, 3).get == Bomb)
    assert(level.cellAt(5, 1).get == Bomb)
    assert(level.cellAt(6, 2).get == Bomb)

  }
  test("horizontal axis reflexion invalid rectangle") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    val oldLevel = levelController.getLevel
    levelController.applyReflection(
      startRow = 5, startCol = 5, endRow = 2, endCol = 4, row = Option(3), col = None, diagonal1 = None, diagonal2 = None, expandMode = Expanding, overlayMode = Transparent, quasiInverse = true
    )
    val newLevel= levelController.getLevel
    assert(oldLevel == newLevel)


  }
  test("horizontal axis reflexion invalid axis") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    val oldLevel = levelController.getLevel
    levelController.applyReflection(
      startRow = 1, startCol = 1, endRow = 2, endCol = 4, row = Option(19), col = None, diagonal1 = None, diagonal2 = None, expandMode = Expanding, overlayMode = Transparent, quasiInverse = true
    )
    val newLevel= levelController.getLevel
    assert(oldLevel == newLevel)


  }
  test("horizontal axis reflexion non expandable") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyReflection(
      startRow = 0, startCol = 0, endRow = 2, endCol = 4, row = Option(3), col = None, diagonal1 = None, diagonal2 = None, expandMode = NonExpanding, overlayMode = Transparent, quasiInverse = false
    )
    val level = levelController.getLevel
    assert(level.rows == 5)
    assert(level.cellAt(4, 3).get == Bomb)


  }
  test("vertical axis reflexion") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyReflection(
      startRow = 0, startCol = 0, endRow = 4, endCol = 1, row = None, col = Option(2), diagonal1 = None, diagonal2 = None, expandMode = NonExpanding, overlayMode = Transparent, quasiInverse = false
    )
    val level = levelController.getLevel
    assert(level.rows == 5)
    assert(level.cellAt(3, 4).get == Bomb)
    assert(level.cellAt(4, 4).get == Bomb)
    assert(level.cellAt(1, 3).get == Bomb)


  }
  test("main diagonal axis reflexion opaque") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyReflection(
      startRow = 0, startCol = 2, endRow = 1, endCol = 3, row = None, col = None, diagonal1 = Option(0, 0), diagonal2 = None, expandMode = NonExpanding, overlayMode = Opaque, quasiInverse = false
    )
    val level = levelController.getLevel
    assert(level.rows == 5)
    assert(level.cellAt(2, 0).get == Bomb)
    assert(level.cellAt(3, 0).get == Empty)

  }
  test("secondary diagonal axis reflexion expandable") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """--#--
        |-#---
        |---#-
        |#----
        |#----""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyReflection(
      startRow = 3, startCol = 0, endRow = 4, endCol = 1, row = None, col = None, diagonal1 = None, diagonal2 = Option(0, 2), expandMode = Expanding, overlayMode = Opaque, quasiInverse = false
    )
    val level = levelController.getLevel
    assert(level.cols == 7)
    assert(level.cellAt(2, 0).get == Bomb)
    assert(level.cellAt(2, 1).get == Bomb)

  }
  test("central symmetry") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """-----#-
        |---#---
        |##---#-
        |--#----
        |-#---#-""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyCentralSymmetry(
      startRow = 0, startCol = 0, endRow = 2, endCol = 2, expandMode = Expanding, overlay = Opaque, pivotRow = 3, pivotCol = 3,  quasiInverse = false
    )
    val level = levelController.getLevel
    assert(level.cellAt(4, 5).get == Bomb)
    assert(level.cellAt(4, 6).get == Bomb)
  }
  test("central symmetry quasi inverse") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """-----#-
        |---#---
        |##---#-
        |--#----
        |-#---#-""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyCentralSymmetry(
      startRow = 0, startCol = 0, endRow = 2, endCol = 2, expandMode = Expanding, overlay = Opaque, pivotRow = 3, pivotCol = 3,  quasiInverse = true
    )
    val level = levelController.getLevel
    assert(level.cellAt(4, 5).get == Bomb)
    assert(level.cellAt(4, 6).get == Bomb)
  }

  test("basic translation") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """##---#-
        |---#---
        |##---#-
        |--#----
        |-#---#-""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyTranslation(
      startRow = 0, startCol = 0, endRow = 1, endCol = 1, newStartRow = 2, newStartCol = 2, expandMode = Expanding, overlay = Transparent, quasiInverse = false
    )
    val level = levelController.getLevel
    assert(level.cellAt(2, 2).get == Bomb)
    assert(level.cellAt(2, 3).get == Bomb)
  }
  test("basic translation quasi inverse") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """##---#-
        |---#---
        |##---#-
        |--#----
        |-#---#-""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.applyTranslation(
      startRow = 0, startCol = 0, endRow = 1, endCol = 1, newStartRow = 2, newStartCol = 2, expandMode = Expanding, overlay = Transparent, quasiInverse = true
    )
    val level = levelController.getLevel
    assert(level.cellAt(0, 0).get == Bomb)
    assert(level.cellAt(0, 1).get == Bomb)
    assert(level.rows == 7)
  }
  test("save central isometry as two rotations and apply it") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """##---#-
        |---#---
        |##---#-
        |--#----
        |-#---#-""".stripMargin)
    writer.close()
    val levelController = LevelController(path = file.getAbsolutePath)
    levelController.addRotation(2, 2, CW)
    levelController.addRotation(2, 2, CW)
    levelController.saveCurrentIsometry("Central symmetry")
    levelController.applySavedIsometry(name = "Central symmetry", startRow = 0, startCol = 0, endRow = 1, endCol = 1, expandMode = Expanding, overlayMode = Transparent, quasiInverse = false)
    val level = levelController.getLevel
    assert(level.cellAt(4, 3).get == Bomb)
    assert(level.cellAt(4, 4).get == Bomb)
  }
  
  test("save single rotation and apply it") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """##---
        |--#--
        |-----
        |-----
        |-----""".stripMargin)
    writer.close()

    val controller1 = LevelController(path = file.getAbsolutePath)
      controller1.applyRotation(startRow = 0, startCol = 0, endRow = 1, endCol = 2, expandMode = NonExpanding, overlay = Transparent, pivotRow = 2, pivotCol = 2, rotationDirection = CW, quasiInverse = false
    )
    val expected = controller1.getLevel

    val controller2 = LevelController(path = file.getAbsolutePath)
    controller2.addRotation(2, 2, CW)
    controller2.saveCurrentIsometry("Single rotation")
    controller2.applySavedIsometry(
      name = "Single rotation",
      startRow = 0, startCol = 0, endRow = 1, endCol = 2,
      expandMode = NonExpanding, overlayMode = Transparent, quasiInverse = false
    )
    val actual = controller2.getLevel

    assert(actual == expected)
  }
  test("saved single rotation quasi inverse behaves like opposite rotation") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#-#--
        |-#---
        |-----
        |#----
        |#----""".stripMargin)
    writer.close()

    val controller1 = LevelController(path = file.getAbsolutePath)
    controller1.applyRotation(
      startRow = 0, startCol = 1, endRow = 1, endCol = 2, expandMode = NonExpanding, overlay = Opaque, pivotRow = 2, pivotCol = 3, rotationDirection = CCW, quasiInverse = false
    )
    val expected = controller1.getLevel

    val controller2 = LevelController(path = file.getAbsolutePath)
    controller2.addRotation(2, 3, CW)
    controller2.saveCurrentIsometry("CW rotation")
    controller2.applySavedIsometry(
      name = "CW rotation",
      startRow = 0, startCol = 1, endRow = 1, endCol = 2,
      expandMode = NonExpanding, overlayMode = Opaque, quasiInverse = true
    )
    val actual = controller2.getLevel

    assert(actual == expected)
  }
}
