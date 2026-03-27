package ui.view

import scalafx.scene.image.ImageView
import utilities.style.{CellStyles, Graphics}

sealed trait LevelCellView (
  val style: String,
  val graphic: Option[ImageView]
)

case class EmptyLevelCellView() extends LevelCellView(
  style = new CellStyles().EmptyLevel,
  graphic = None
)

case class MineLevelCellView() extends LevelCellView(
  style = new CellStyles().MineLevel,
  graphic = Some (Graphics().Mine)
)

case class SelectedRectangle() extends LevelCellView(
  style = new CellStyles().SelectedRectangle,
  graphic = None
)

case class SelectedPicture() extends LevelCellView(
  style = new CellStyles().SelectedPicture,
  graphic = None
)

case class PivotCellView() extends LevelCellView(
  style = new CellStyles().Pivot,
  graphic = None
)

case class ReflectionAxisCellView() extends LevelCellView(
  style = new CellStyles().ReflexionAxis,
  graphic = None
)

