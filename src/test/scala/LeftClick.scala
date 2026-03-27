import logic.controllers.GameController
import model.{Empty, Hidden, Lost, Number, Playing, Revealed, Won}
import org.scalatest.funsuite.AnyFunSuite

import java.io.{File, PrintWriter}

class LeftClick extends AnyFunSuite {
  
  test("left click on mine field finish the level") {

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
    gameController.leftClick(0, 0)
    assert(gameController.getState.status == Lost)

  }
  test("left click on numbered safe field reveals that field"){
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
    gameController.leftClick(0, 1)
    assert(gameController.getState.status == Playing)
    assert(gameController.getState.board.cellAt(0, 1).contains(Number(2)) )
    assert(gameController.getState.board.cellStatusAt(0 ,1).contains(Revealed) )

  }
  test("left click on zero field reveals flood area") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#--#-
        |-#--#
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val gameController = GameController(levelPath = Some(file.getAbsolutePath), testing = true)
    gameController.leftClick(3, 4)
    val board = gameController.getState.board
    assert(gameController.getState.status == Playing)
    assert(board.cellAt(3, 4).contains(Number(0)))
    assert(board.cellStatusAt(3, 4).contains(Revealed))
    assert(board.cellStatusAt(3, 3).contains(Revealed))
    assert(board.cellStatusAt(2, 4).contains(Revealed))
    assert(board.cellStatusAt(4, 4).contains(Revealed))

  } 
  test("left click on all safe fields finish the game") {
    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#--#-
        |-#--#
        |#####
        |#-#--
        |##---""".stripMargin)
    writer.close()
    val gameController = GameController(levelPath = Some(file.getAbsolutePath), testing = true)
    gameController.leftClick(0, 1)
    gameController.leftClick(0, 2)
    gameController.leftClick(0, 4)
    gameController.leftClick(1, 0)
    gameController.leftClick(1, 2)
    gameController.leftClick(1, 3)
    gameController.leftClick(3, 1)
    gameController.leftClick(3, 3)
    gameController.leftClick(3, 4)
    gameController.leftClick(4, 2)
    gameController.leftClick(4, 3)
    gameController.leftClick(4, 4)
    assert(gameController.getState.status == Won)


  }
}
