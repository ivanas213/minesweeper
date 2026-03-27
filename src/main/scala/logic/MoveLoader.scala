package logic

import java.io.File

import scala.io.Source
import model.{Move, MoveType}

object MoveLoader {

  def loadMoves(file: File): Seq[Move] = {

    val source = Source.fromFile(file)

    val moves =
      try {
        source.getLines().toVector.map { line =>

          val trimmed = line.trim

          val moveChar = trimmed.charAt(0)   
          val coordinatesStr = trimmed.substring(2, trimmed.length - 1)
          val coordinates = coordinatesStr.split(",")
          val row = coordinates(0).toInt - 1  
          val col = coordinates(1).toInt - 1
          val moveType =
            if (moveChar == 'L') MoveType.Left
            else if (moveChar == 'D') MoveType.Right
            else throw new Exception("Invalid input file")

          Move(row, col, moveType)
        }
      } finally {
        source.close()
      }

    moves
  }
}
