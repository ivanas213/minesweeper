import logic.LevelLoader.loadLevel
import org.scalatest.funsuite.AnyFunSuite

import java.io.{File, PrintWriter}

class LoadLevel extends AnyFunSuite{
  test("loadLevel loads valid level") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()

    val level = loadLevel(file.getAbsolutePath)

    assert(level.rows == 5)
    assert(level.cols == 5)
    assert(level.mines == 6)
  }
  test("loadLevel throws exception when level is not rectangular") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#---
        |-----
        |#-#--
        |##---#""".stripMargin)
    writer.close()

    intercept[Exception] {
      loadLevel(file.getAbsolutePath)
    }
  }
  test("loadLevel throws exception when the level is not valid because it does not belong to any of the 3 difficulties") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#--
        |---""".stripMargin)
    writer.close()
    writer.close()

    intercept[Exception] {
      loadLevel(file.getAbsolutePath)
    }
  }
  test("loadLevel throws exception when the level is not valid because there is invalid caracter") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    writer.write(
      """#----
        |-#X--
        |-----
        |#-#--
        |##---""".stripMargin)
    writer.close()

    intercept[Exception] {
      loadLevel(file.getAbsolutePath)
    }
  }
  test("loadLevel throws exception when the level file is empty") {

    val file = File.createTempFile("level", ".txt")
    val writer = new PrintWriter(file)

    intercept[Exception] {
      loadLevel(file.getAbsolutePath)
    }
  }
}


