package logic

import model.{Beginner, Board, Cell, CellStatus, Expert, Flagged, GameState, Hidden, Intermediate, Mine, Number, Revealed}

import java.io.{File, PrintWriter}
import scala.io.Source
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object GameSaverLoader {
  private def serializeBoardCells(board: Board): String =
    (for {
      row <- 0 until board.rows
    } yield {
      (for {
        col <- 0 until board.cols
      } yield {
        board.cellAt(row, col) match
          case Some(Number(value)) => s"${value.toString}"
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
    val now = LocalDateTime.now()
    val currentTime = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    val directory = new File("saved")
    if (!directory.exists())
      directory.mkdir()
    val file = new File(s"saved/$name.json")

    val writer = new PrintWriter(file)
    val difficulty = gameState.board.difficulty
    val difficultyString =
      difficulty match
        case Beginner => "B"
        case Intermediate => "I"
        case Expert => "E"
        case _ => throw new Exception("Unknown difficulty")
        
    val json =
      s"""
         |{
         |  "rows": ${gameState.board.rows},
         |  "cols": ${gameState.board.cols},
         |  "elapsedTime": ${gameState.time},
         |  "flagsLeft": ${gameState.flags},
         |  "clicks" : ${gameState.clicks},
         |  "totalHints":${gameState.totalHintsUsed},
         |  "probHints": ${gameState.probabilisticHintsUsed},
         |  "boardCells": "${serializeBoardCells(gameState.board).replace("\n", "\\n")}",
         |  "boardStatuses": "${serializeBoardStatuses(gameState.board).replace("\n", "\\n")}",
         |  "difficulty": "$difficultyString",
         |  "savedAt" : "$currentTime"
         |}
         |""".stripMargin

    writer.write(json)
    writer.close()
  }


  def getSavedGamesNames: Seq[(String, LocalDateTime)] = {
    val dir = new File("saved")

    if (!dir.exists() || !dir.isDirectory)
      Seq.empty
    else {
      dir.listFiles()
        .filter(file => file.isFile && file.getName.endsWith(".json"))
        .flatMap { file =>
          val source = Source.fromFile(file)
          val content = try source.mkString finally source.close()

          val pattern = "\"savedAt\"\\s*:\\s*\"([^\"]*)\"".r

          pattern.findFirstMatchIn(content).map { m =>
            val dateTime =
              LocalDateTime.parse(
                m.group(1),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
              )

            val name = file.getName.stripSuffix(".json")
            (name, dateTime)
          }
        }
        .toSeq
        .sortBy(_._2)(Ordering[LocalDateTime].reverse)
    }
  }

  def loadGame(name: String): (GameState, LocalDateTime) = {
    val file = new File(s"saved/$name.json")
    if (!file.exists())
      throw new Exception("Save file not found")

    val source = scala.io.Source.fromFile(file)
    val content = try source.mkString finally source.close()

    def extractInt(pattern: String): Int =
      pattern.r.findFirstMatchIn(content) match
        case Some(m) => m.group(1).toInt
        case None => throw new Exception(s"Missing field")

    def extractString(pattern: String): String =
      pattern.r.findFirstMatchIn(content) match
        case Some(m) => m.group(1).replace("\\n", "\n")
        case None => throw new Exception(s"Missing field")

    def extractDifficulty(pattern: String): String =
      pattern.r.findFirstMatchIn(content) match
        case Some(m) => m.group(1)
        case None => throw new Exception("Missing difficulty")

    val rows = extractInt("\"rows\"\\s*:\\s*(\\d+)")
    val cols = extractInt("\"cols\"\\s*:\\s*(\\d+)")

    val elapsedTime = extractInt("\"elapsedTime\"\\s*:\\s*(\\d+)")
    val flagsLeft = extractInt("\"flagsLeft\"\\s*:\\s*(\\d+)")
    val clicks = extractInt("\"clicks\"\\s*:\\s*(\\d+)")
    val totalHints = extractInt("\"totalHints\"\\s*:\\s*(\\d+)")
    val probHints = extractInt("\"probHints\"\\s*:\\s*(\\d+)")

    val boardCellsRaw =
      extractString("\"boardCells\"\\s*:\\s*\"([^\"]*)\"")

    val boardStatusesRaw =
      extractString("\"boardStatuses\"\\s*:\\s*\"([^\"]*)\"")
      
    val difficultyRaw =
      extractDifficulty("\"difficulty\"\\s*:\\s*\"([BIE])\"")
    val savedAtRaw =
      extractString("\"savedAt\"\\s*:\\s*\"([^\"]*)\"")

    val savedAt =
      LocalDateTime.parse(savedAtRaw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val cellLines = boardCellsRaw.split("\n")
    val statusLines = boardStatusesRaw.split("\n")

    val cells: Vector[Vector[Cell]] =
      cellLines.toVector.map { line =>
        line.toVector.map {
          case '#' => Mine
          case digit if digit.isDigit => Number(digit.asDigit)
          case _ => throw new Exception("Invalid cell")
        }
      }

    val statuses: Vector[Vector[CellStatus]] =
      statusLines.toVector.map { line =>
        line.toVector.map {
          case 'H' => Hidden
          case 'F' => Flagged
          case 'R' => Revealed
          case _ => throw new Exception("Invalid status")
        }
      }
    val difficulty =
      difficultyRaw match
        case "B" => Beginner
        case "I" => Intermediate
        case "E" => Expert
        case _ => throw new Exception("Invalid difficulty")

    val board = Board(cells, statuses, difficulty)

    val gameState = GameState(
      board = board,
      flags = flagsLeft,
      time = elapsedTime,
      clicks = clicks,
      totalHintsUsed = totalHints,
      probabilisticHintsUsed = probHints
    )
    (gameState, savedAt)
  }


}
// TODO srediti ovaj load da bude lepsi