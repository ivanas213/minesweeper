package ui

import utilities.{Graphics, CellStyles}
import scalafx.scene.image.{Image, ImageView}

sealed trait CellView (
                        val text: String,
                        val style: String,
                        val graphic: Option[ImageView]
                      )

case class HiddenCellView() extends CellView(
  text = "",
  style = CellStyles.HiddenCell,
  graphic = None
)

case class MineToRevealCellView() extends CellView(
  text = "",
  style = CellStyles.MineToRevealStyle,
  graphic = Some (Graphics.Mine)
)

case class MineCellView() extends CellView(
  text = "",
  style =  CellStyles.Mine,
  graphic = Some (Graphics.Mine)
)

case class FlaggedCellView() extends CellView(
  text = "",
  style = CellStyles.Flagged,
  graphic = Some (Graphics.Flag)
)

case class EmptyRevealedCellView (neighbors: Int) extends CellView(
  text = if (neighbors == 0) "" else neighbors.toString,
  style = CellStyles.Empty(neighbors),
  graphic = None
)
