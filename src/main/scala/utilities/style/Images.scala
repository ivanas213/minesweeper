package utilities.style

import scalafx.scene.image.Image

object Images {

  private def load(path: String): Image =
    new Image(getClass.getResourceAsStream(path))

  val MineImg: Image = load("/mine.png")
  val FlagImg: Image = load("/flag.png")
  val DifficultyImg: Image = load("/difficulty.png")
  val HappySmileImg = new Image("/smile.png")
  val SadSmileImg = new Image("/sad.png")
  val VeryHappyImg = new Image("/very-happy.png")
  val HintImg = new Image("/hint.png")
  val StartGame = new Image("/startgame.png")
  val RotateCW = new Image("/rotateCW.png")
  val RotateCCW = new Image("/rotateCCW.png")
  val RectangleSelect = new Image("/rectangle.png")
  val PivotSelect = new Image("/rotate.png")

}
