package logic

import model.{Board, Flagged, Hidden, Mine, Revealed, Number}

import java.io.{File, PrintWriter}

class GameSaver {
  private def serializeBoardCells(board: Board): String =
    (for {
      row <- 0 until board.rows
    } yield {
      (for {
        col <- 0 until board.cols
      } yield {
        board.cellAt(row, col) match
          case Some(Number(value)) => "-"
          case Some(Mine)          => "#"
          case _ => throw new Exception("Invalid cell")
      }).mkString("")
    }).mkString("\n")


  private def serializeBoardStatuses(board: Board): String =
    (for {
      row <- 0 until board.rows
    } yield {
      (for {
        col <- 0 until board.cols
      } yield {
        board.cellStatusAt(row, col) match
          case Some(Hidden) => "H"
          case Some(Flagged) => "F"
          case Some(Revealed) => "R"
          case None => throw new Exception() // TODO
      }).mkString("")
    }).mkString("\n")

  def saveGame(name: String, gameState: GameState): Unit= {
    val directory = new File("saved")
    if (!directory.exists())
      directory.mkdir()
    val file = new File(s"saved/$name.json")

    val writer = new PrintWriter(file)

    val json =
      s"""
         |{
         |  "rows": ${gameState.board.rows},
         |  "cols": ${gameState.board.rows},
         |  "elapsedTime": ${gameState.time},
         |  "flagsLeft": ${gameState.flags},
         |  "clicks" : ${gameState.clicks},
         |  "totalHints":${gameState.totalHintsUsed},
         |  "probHints": ${gameState.probabilisticHintsUsed},
         |  "boardCells": "${serializeBoardCells(gameState.board).replace("\n", "\\n")}",
         |  "boardStatuses": "${serializeBoardStatuses(gameState.board).replace("\n", "\\n")}"
         |}
         |""".stripMargin

    writer.write(json)
    writer.close()
  }
}
