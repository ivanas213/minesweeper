package utilities

import scalafx.scene.image.ImageView
import utilities.Images.{HappySmileImg, HintImg}

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
  val SmileView: ImageView = new ImageView(HappySmileImg) {
    fitWidth = 28 
    fitHeight = 28
    preserveRatio = true
    smooth = true

  }
  
  val HintView: ImageView = new ImageView(HintImg) {
    fitWidth = 28 
    fitHeight = 28
    preserveRatio = true
    smooth = true

  }
}
