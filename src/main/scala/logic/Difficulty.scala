package logic

import utilities.{BeginnerConstants, ExpertConstants, IntermediateConstants}

sealed trait Difficulty {
  def name: String
  def levels: Vector[LevelParameters]
  def minRows: Int
  def maxRows: Int
  def minCols: Int
  def maxCols: Int
  def minMineRatio: Double
  def maxMineRatio : Double
  def basePath:String
}
case object Beginner extends Difficulty{
  val name = "Почетни ниво"
  val basePath = "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner"
  val levels: Vector[LevelParameters] = Vector(
    LevelParameters("Ниво 1", s"${basePath}\\level1.txt", Beginner),
    LevelParameters("Ниво 2", s"${basePath}\\level2.txt", Beginner),
    LevelParameters("Ниво 3", s"${basePath}\\level3.txt", Beginner),
    LevelParameters("Ниво 4", s"${basePath}\\level4.txt", Beginner),
    LevelParameters("Ниво 5", s"${basePath}\\level5.txt", Beginner),
    LevelParameters("Ниво 6", s"${basePath}\\level6.txt", Beginner),
    LevelParameters("Ниво 7", s"${basePath}\\level7.txt", Beginner),
    LevelParameters("Ниво 8", s"${basePath}\\level8.txt", Beginner),
    LevelParameters("Ниво 9", s"${basePath}\\level9.txt", Beginner),
    LevelParameters("Ниво 10", s"${basePath}\\level10.txt", Beginner),
    LevelParameters("Ниво 11", s"${basePath}\\level11.txt", Beginner),
    LevelParameters("Ниво 12", s"${basePath}\\level12.txt", Beginner),
    LevelParameters("Ниво 13", s"${basePath}\\level13.txt", Beginner),
    LevelParameters("Ниво 14", s"${basePath}\\level14.txt", Beginner),


  )
  val minRows: Int = BeginnerConstants.minRows
  val maxRows: Int = BeginnerConstants.maxRows 
  val minCols: Int = BeginnerConstants.minColumns
  val maxCols: Int = BeginnerConstants.maxColumns
  val minMineRatio: Double = BeginnerConstants.minMineRatio
  val maxMineRatio: Double = BeginnerConstants.maxMineRatio
}
case object Intermediate extends Difficulty{
  val name = "Средњи ниво"
  val basePath = "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\intermediate"

  val levels: Vector[LevelParameters] = Vector(
    LevelParameters("Ниво 1", s"${basePath}\\level1.txt", Intermediate),
    LevelParameters("Ниво 2", s"${basePath}\\level2.txt", Intermediate)
  )
  val minRows: Int = IntermediateConstants.minRows
  val maxRows: Int = IntermediateConstants.maxRows
  val minCols: Int = IntermediateConstants.minColumns
  val maxCols: Int = IntermediateConstants.maxColumns
  val minMineRatio: Double = IntermediateConstants.minMineRatio
  val maxMineRatio: Double = IntermediateConstants.maxMineRatio
}

case object Expert extends Difficulty{
  val name = "Напредни ниво"
  val basePath = "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\expert"
  val levels: Vector[LevelParameters] = Vector(
    LevelParameters("Ниво 1", s"${basePath}\\level1.txt", Expert),
    LevelParameters("Ниво 2", s"${basePath}\\level2.txt", Expert)
  )
  val minRows: Int = ExpertConstants.minRows
  val maxRows: Int = ExpertConstants.maxRows
  val minCols: Int = ExpertConstants.minColumns
  val maxCols: Int = ExpertConstants.maxColumns
  val minMineRatio: Double = ExpertConstants.minMineRatio
  val maxMineRatio: Double = ExpertConstants.maxMineRatio
}
