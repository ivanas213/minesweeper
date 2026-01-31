package utilities

import scalafx.scene.image.Image

object Images {

  private def load(path: String): Image =
    new Image(getClass.getResourceAsStream(path))

  val MineImg: Image = load("/mine.png")
  val FlagImg: Image = load("/flag.png")
  val DifficultyImg: Image = load("/difficulty.png")
}
