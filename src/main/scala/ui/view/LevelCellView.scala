package ui.view

import scalafx.scene.image.ImageView
import utilities.{CellStyles, Graphics}

sealed trait LevelCellView (
  val style: String,
  val graphic: Option[ImageView]
)

case class EmptyLevelCellView() extends LevelCellView(
  style = new CellStyles().HiddenCell,
  graphic = None
)

case class MineLevelCellView() extends LevelCellView(
  style = new CellStyles().MineToRevealStyle,
  graphic = Some (Graphics().Mine)
)

case class SelectedToClearCellView() extends LevelCellView(
  style = new CellStyles().SelectedToClear,
  graphic = None
)
