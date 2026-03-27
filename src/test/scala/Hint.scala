import org.scalatest.funsuite.AnyFunSuite
import logic.controllers.GameController
import model.{Empty, Flagged, Hidden, Lost, Number, Playing, Revealed, Won}
import java.io.{File, PrintWriter}
class Hint extends AnyFunSuite{
  test("hint returns a probabilistically safe hidden cell when no guaranteed safe cell exists") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#---#
        |-##-#
        |--#--
        |-----""".stripMargin)
    writer.close()
    val gameController = GameController(levelPath = Some(file.getAbsolutePath), testing = true)
    gameController.leftClick(0, 1)
    gameController.rightClick(0, 0)
    gameController.rightClick(1, 2)
    val hintCell = gameController.getHintCoordinates.get
    assert(hintCell != (0, 1) && hintCell != (0, 0) && hintCell != (1, 1) && hintCell != (1, 0) && hintCell != (1, 2) && hintCell != (0, 2) )

  }
  test("hint returns one of guaranteed safe cells when such cells exist") {

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
    gameController.rightClick(0, 0)
    gameController.rightClick(0, 2)
    val hintCell = gameController.getHintCoordinates.get
    assert(hintCell == (0, 2) || hintCell == (1, 2) || hintCell == (1, 0))

  }
  test("hint returns none when game is finished") {

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

    assert(gameController.getHintCoordinates.isEmpty)

  }
}
