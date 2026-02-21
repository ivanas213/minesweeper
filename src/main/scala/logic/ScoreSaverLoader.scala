package logic

import java.io.{File, PrintWriter}


import java.io.{File, PrintWriter}
import scala.io.Source

class ScoreSaverLoader {

  def loadResults(difficulty: Difficulty): Seq[model.Score] = {
    val dir = new File("results")
    if (!dir.exists()) dir.mkdir()

    val suffix = difficulty match
      case Beginner => "B"
      case Intermediate => "I"
      case Expert => "E"

    val file = new File(s"results/leaderboard_$suffix.json")
    if (!file.exists()) return Seq.empty


    val source = Source.fromFile(file)
    val content = try source.mkString finally source.close()

    val entryPattern =
      """\{\s*"name"\s*:\s*"([^"]+)",\s*"score"\s*:\s*(\d+)\s*\}""".r


    entryPattern.findAllMatchIn(content).map { m =>
      model.Score(
        name = m.group(1),
        score = m.group(2).toInt
      )
    }.toSeq.sortBy(-_.score)
  }


  def saveResult(difficulty: Difficulty, name: String, score: Int): Unit = {

    val dir = new File("results")
    if (!dir.exists()) dir.mkdir()

    val suffix = difficulty match
      case Beginner => "B"
      case Intermediate => "I"
      case Expert => "E"

    val file = new File(s"results/leaderboard_$suffix.json")

    val existing =
      if (file.exists()) loadResults(difficulty)
      else Seq.empty

    val updated =
      (existing :+ model.Score(name, score))
        .sortBy(-_.score)
        .take(5) 

    val writer = new PrintWriter(file)

    val json =
      updated.map { s =>
        s"""{
           |  "name": "${s.name}",
           |  "score": ${s.score}
           |}""".stripMargin
      }.mkString("[\n", ",\n", "\n]")

    writer.write(json)
    writer.close()
  }
}
