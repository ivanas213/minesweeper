package ui

import utilities.{Graphics, Styles}
import scalafx.scene.image.{Image, ImageView}
import ui.utilities.Colors
import ui.utilities.Images.{FlagImg, MineImg}


sealed trait CellView (
                        val text: String,
                        val style: String,
                        val graphic: Option[ImageView]
                      )

case class HiddenCellView() extends CellView(
  text = "",
  style = Styles.HiddenCell,
  graphic = None
)

case class MineToRevealCellView() extends CellView(
  text = "",
  style = Styles.MineToRevealStyle,
  graphic = Some (Graphics.Mine)
)

case class MineCellView() extends CellView(
  text = "",
  style =  Styles.Mine,
  graphic = Some (Graphics.Mine)
)

case class FlaggedCellView() extends CellView(
  text = "",
  style = Styles.Flagged,
  graphic = Some (Graphics.Flag)
)

case class EmptyRevealedCellView (neighbors: Int) extends CellView(
  text = if (neighbors == 0) "" else neighbors.toString,
  style = Styles.Empty,
  graphic = None
)
