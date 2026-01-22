package `enum`

sealed trait DifficultyConstants{
  val minRows:Int
  val maxRows:Int
  val minColumns:Int
  val maxColumns:Int
  val minMineRatio:Int
  val maxMineRatio:Int
}
case object Beginner extends Difficulty{
  override val minRows = 5
  override val maxRows = 9
  override val minColumns = 5
  override val maxColumns = 9
  override val minMineRatio = 10
  override val maxMineRatio = 15
}

case object Intermediate extends Difficulty {
  override val minRows = 10
  override val maxRows = 15
  override val minColumns = 10
  override val maxColumns = 15
  override val minMineRatio = 15
  override val maxMineRatio = 20
}

case object Expert extends Difficulty {
  override val minRows = 16
  override val maxRows = 30
  override val minColumns = 16
  override val maxColumns = 30
  override val minMineRatio = 20
  override val maxMineRatio = 25
}
