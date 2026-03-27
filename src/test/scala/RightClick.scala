import org.scalatest.funsuite.AnyFunSuite
import logic.controllers.GameController
import model.{Empty, Flagged, Hidden, Lost, Number, Playing, Revealed, Won}

import java.io.{File, PrintWriter}

class RightClick extends AnyFunSuite{
  test("right click on field makes it flagged") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val gameController = GameController(levelPath = Some(file.getAbsolutePath), testing = true)
    gameController.rightClick(0, 0)
    assert(gameController.getState.status == Playing)
    assert(gameController.getState.board.cellStatusAt(0, 0).get == Flagged)

  }
  test("two rights click on the same cells makes it hidden again") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val gameController = GameController(levelPath = Some(file.getAbsolutePath), testing = true)
    gameController.rightClick(0, 0)
    gameController.rightClick(0, 0)
    assert(gameController.getState.status == Playing)
    assert(gameController.getState.board.cellStatusAt(0, 0).get == Hidden)

  }
  test("left click on the flagged cell doesn't do anything") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val gameController = GameController(levelPath = Some(file.getAbsolutePath), testing = true)
    gameController.rightClick(0, 0)
    gameController.leftClick(0, 0)
    assert(gameController.getState.status == Playing)
    assert(gameController.getState.board.cellStatusAt(0, 0).get == Flagged)

  }
}
