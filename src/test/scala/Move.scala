import org.scalatest.funsuite.AnyFunSuite
import java.io.{File, PrintWriter}
import logic.controllers.GameController
import model.{Flagged, Hidden, Lost, Revealed}

class Move extends AnyFunSuite{
  test("loadMoves executes left and right clicks from file") {

    val levelFile = File.createTempFile("level", ".txt")
    val levelWriter = new PrintWriter(levelFile)

    levelWriter.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin
    )
    levelWriter.close()
    val movesFile = File.createTempFile("moves", ".txt")
    val movesWriter = new PrintWriter(movesFile)
    movesWriter.write(
      """L(1,2)
        |D(1,1)""".stripMargin
    )
    movesWriter.close()
    val controller = GameController(
      levelPath = Some(levelFile.getAbsolutePath),
      testing = true
    )
    controller.loadMoves(movesFile)
    val board = controller.getState.board
    assert(board.cellStatusAt(0, 1).contains(Revealed))
    assert(board.cellStatusAt(0, 0).contains(Flagged))
  }
  test("loadMoves with two successive right clicks on the same cell makes it hidden again") {

    val levelFile = File.createTempFile("level", ".txt")
    val levelWriter = new PrintWriter(levelFile)

    levelWriter.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin
    )
    levelWriter.close()
    val movesFile = File.createTempFile("moves", ".txt")
    val movesWriter = new PrintWriter(movesFile)
    movesWriter.write(
      """D(1,1)
        |D(1,1)""".stripMargin
    )
    movesWriter.close()
    val controller = GameController(
      levelPath = Some(levelFile.getAbsolutePath),
      testing = true
    )
    controller.loadMoves(movesFile)
    val board = controller.getState.board
    assert(board.cellStatusAt(0, 0).contains(Hidden))
  }
  test("loadMoves doesn't execute remaining moves if any move leads to the end of game") {

    val levelFile = File.createTempFile("level", ".txt")
    val levelWriter = new PrintWriter(levelFile)

    levelWriter.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin
    )
    levelWriter.close()
    val movesFile = File.createTempFile("moves", ".txt")
    val movesWriter = new PrintWriter(movesFile)
    movesWriter.write(
      """L(1,1)
        |D(2,2)""".stripMargin
    )
    movesWriter.close()
    val controller = GameController(
      levelPath = Some(levelFile.getAbsolutePath),
      testing = true
    )
    controller.loadMoves(movesFile)
    val board = controller.getState.board
    assert(controller.getState.status == Lost)
    assert(!board.cellStatusAt(1, 1).contains(Flagged))
  }
}
