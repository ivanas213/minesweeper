import logic.LevelLoader.{getDifficulty, getLevelsByDifficulty, getRandomLevel, isValid, loadLevel}
import model.{Beginner, Empty, Expert, Intermediate, Level, LevelParameters, Mine}
import org.scalatest.funsuite.AnyFunSuite

import java.io.{File, PrintWriter}

class Difficulty extends AnyFunSuite{

  test("9x9 board is Beginner difficulty") {
    val diff = getDifficulty(9, 9)
    assert(diff == Beginner)
  }
  test("12x14 board is Intermediate difficulty") {
    val diff = getDifficulty(12, 14)
    assert(diff == Intermediate)
  }
  test("20x20 board is Expert difficulty") {
    val diff = getDifficulty(20, 20)
    assert(diff == Expert)
  }
  test("4x4 has too low rows and columns") {
    intercept[Exception] {
      getDifficulty(4, 4)    
    }
  } 
  test("35x35 has too low rows and columns") {
    intercept[Exception] {
      getDifficulty(35, 35)    
    }
  }
  test("valid beginner level passes validation") {
    val valid = isValid(rows = 9, cols = 9, mines = 10, Beginner)
    assert(valid)
  }

  test("too many mines for beginner is invalid") {
    val valid = isValid(rows = 9, cols = 9, mines = 50, Beginner)
    assert(!valid)
  }

  test("too large board for beginner is invalid") {
    val valid = isValid(rows = 30, cols = 30, mines = 10, Beginner)
    assert(!valid)
  }
  test("getLevelsByDifficulty returns only beginner levels") {
    val levels = getLevelsByDifficulty(Beginner)

    assert(levels.forall(_.difficulty == Beginner))
  }
  test("getLevelsByDifficulty returns only intermediate levels") {
    val levels = getLevelsByDifficulty(Intermediate)

    assert(levels.forall(_.difficulty == Intermediate))
  }
  test("getLevelsByDifficulty returns only expert levels") {
    val levels = getLevelsByDifficulty(Expert)

    assert(levels.forall(_.difficulty == Expert))
  }
  test("get random beginner level returns random beginner level") {
    val levels = getLevelsByDifficulty(Beginner)

    assert(getRandomLevel(levels).difficulty == Beginner)
  }
  test("random beginner level comes from beginnner levels list") {
    val levels = getLevelsByDifficulty(Beginner)
    val level = getRandomLevel(levels)

    assert(levels.contains(level))
  }
  test("random level works with single element") {

    val levelParameters =
      LevelParameters("levelName", "somePath", Beginner)

    val result = getRandomLevel(Vector(levelParameters))

    assert(result == levelParameters)
  }
}
