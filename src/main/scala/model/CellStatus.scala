package model

sealed trait CellStatus{

  def reveal(): CellStatus = this match
    case Hidden => Revealed
    case Flagged => Flagged
    case Revealed => Revealed

  def toggleFlag(): CellStatus =
    this match
      case Flagged => Hidden
      case Hidden  => Flagged
      case _       => this

}
case object Hidden extends  CellStatus
case object Flagged extends  CellStatus
case object Revealed extends  CellStatus