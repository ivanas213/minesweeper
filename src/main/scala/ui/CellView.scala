package ui

import utilities.{Graphics, CellStyles}
import scalafx.scene.image.ImageView

sealed trait CellView (
                        val text: String,
                        val style: String,
                        val graphic: Option[ImageView]
                      )

case class HiddenCellView() extends CellView(
  text = "",
  style = new CellStyles().HiddenCell,
  graphic = None
)

case class MineToRevealCellView() extends CellView(
  text = "",
  style = CellStyles().MineToRevealStyle,
  graphic = Some (Graphics().Mine)
)

case class MineCellView() extends CellView(
  text = "",
  style =  new CellStyles().Mine,
  graphic = Some (Graphics().Mine)
)

case class FlaggedCellView() extends CellView(
  text = "",
  style = new CellStyles().Flagged,
  graphic = Some (Graphics().Flag)
)

case class EmptyRevealedCellView (neighbors: Int) extends CellView(
  text = if (neighbors == 0) "" else neighbors.toString,
  style = new CellStyles().EmptyRevealed(neighbors),
  graphic = None
)
