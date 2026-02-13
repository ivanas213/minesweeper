package services

import scalafx.animation.{KeyFrame, Timeline}
import scalafx.util.Duration

class GameTimer(onTick: Int => Unit) {

  private var seconds = 0

  private val timeline = new Timeline {
    cycleCount = Timeline.Indefinite
    keyFrames = Seq(
      KeyFrame(
        time = Duration(1000),
        onFinished = _ => {
          seconds += 1
          onTick(seconds)
        }
      )
    )
  }

  def start(): Unit =
    timeline.play()

  def stop(): Unit =
    timeline.stop()

  def reset(): Unit = {
    stop()
    seconds = 0
    onTick(seconds)
  }

  def current: Int = seconds
}
