package model

sealed trait CellStatus 
case object Hidden extends  CellStatus
case object Flagged extends  CellStatus
case object Revealed extends  CellStatus