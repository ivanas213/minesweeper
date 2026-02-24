package utilities.style

import scalafx.scene.image.ImageView

class Graphics {
  def Mine: ImageView = new ImageView(Images.MineImg) {
    fitWidth = 14
    fitHeight = 14
    preserveRatio = true
  }
  def Flag: ImageView = new ImageView(Images.FlagImg) {
    fitWidth = 12
    fitHeight = 12
    preserveRatio = true
  }
  def SmileView: ImageView = new ImageView(Images.HappySmileImg) {
    fitWidth = 28 
    fitHeight = 28
    preserveRatio = true
    smooth = true

  }
  
  def HintView: ImageView = new ImageView(Images.HintImg) {
    fitWidth = 28 
    fitHeight = 28
    preserveRatio = true
    smooth = true

  }
}
