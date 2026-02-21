package utilities

sealed trait DifficultyConstants{
  val minRows:Int
  val maxRows:Int
  val minColumns:Int
  val maxColumns:Int
  val minMineRatio:Int
  val maxMineRatio:Int
}
case object BeginnerConstants extends DifficultyConstants {
  override val minRows = 4
  override val maxRows = 9
  override val minColumns = 5
  override val maxColumns = 9
  override val minMineRatio = 10
  override val maxMineRatio = 15
}

case object IntermediateConstants extends DifficultyConstants {
  override val minRows = 10
  override val maxRows = 15
  override val minColumns = 10
  override val maxColumns = 15
  override val minMineRatio = 15
  override val maxMineRatio = 20
}

case object ExpertConstants extends DifficultyConstants {
  override val minRows = 16
  override val maxRows = 30
  override val minColumns = 16
  override val maxColumns = 30
  override val minMineRatio = 20
  override val maxMineRatio = 25
}

// TODO uvek su pravougane pa mozda je suvisno imati i rows i cols u konstantama
// TODO proveriti da li su mi svi nivoi u opsegu i mozda popraviti min broj redova na 5