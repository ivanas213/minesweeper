package utilities

import scalafx.scene.image.ImageView

object Graphics {
  val Mine: ImageView = new ImageView(Images.MineImg) {
    fitWidth = 14
    fitHeight = 14
    preserveRatio = true
  }
  val Flag: ImageView = new ImageView(Images.FlagImg) {
    fitWidth = 12
    fitHeight = 12
    preserveRatio = true
  }
}
