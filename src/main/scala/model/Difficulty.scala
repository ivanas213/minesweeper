package model

import utilities.enumeration.{BeginnerConstants, ExpertConstants, IntermediateConstants}
import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters.*

sealed trait Difficulty {
  def name: String
  def levels: Vector[LevelParameters]
  def minRows: Int
  def maxRows: Int
  def minCols: Int
  def maxCols: Int
  def minMineRatio: Double
  def maxMineRatio: Double
  def basePath: String

  protected def loadLevels(currentDifficulty: Difficulty): Vector[LevelParameters] = {
    val path = Paths.get(basePath)

    if (!Files.exists(path) || !Files.isDirectory(path)) Vector.empty
    else {
      Files.list(path).iterator().asScala
        .filter(p => Files.isRegularFile(p) && p.toString.endsWith(".txt"))
        .toVector
        .sortBy(_.getFileName.toString)
        .map { p =>
          val fileName = p.getFileName.toString
          val levelName = fileName.stripSuffix(".txt")
          LevelParameters(levelName, p.toString, currentDifficulty)
        }
    }
  }
}

case object Beginner extends Difficulty {
  val name = "Почетни ниво"
  val basePath = "levels\\beginner"

  def levels: Vector[LevelParameters] = loadLevels(this)

  val minRows: Int = BeginnerConstants.minRows
  val maxRows: Int = BeginnerConstants.maxRows
  val minCols: Int = BeginnerConstants.minColumns
  val maxCols: Int = BeginnerConstants.maxColumns
  val minMineRatio: Double = BeginnerConstants.minMineRatio
  val maxMineRatio: Double = BeginnerConstants.maxMineRatio
}

case object Intermediate extends Difficulty {
  val name = "Средњи ниво"
  val basePath = "levels\\intermediate"

  def levels: Vector[LevelParameters] = loadLevels(this)

  val minRows: Int = IntermediateConstants.minRows
  val maxRows: Int = IntermediateConstants.maxRows
  val minCols: Int = IntermediateConstants.minColumns
  val maxCols: Int = IntermediateConstants.maxColumns
  val minMineRatio: Double = IntermediateConstants.minMineRatio
  val maxMineRatio: Double = IntermediateConstants.maxMineRatio
}

case object Expert extends Difficulty {
  val name = "Напредни ниво"
  val basePath = "levels\\expert"

  def levels: Vector[LevelParameters] = loadLevels(this)

  val minRows: Int = ExpertConstants.minRows
  val maxRows: Int = ExpertConstants.maxRows
  val minCols: Int = ExpertConstants.minColumns
  val maxCols: Int = ExpertConstants.maxColumns
  val minMineRatio: Double = ExpertConstants.minMineRatio
  val maxMineRatio: Double = ExpertConstants.maxMineRatio
}