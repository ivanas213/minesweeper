package model

sealed trait CellType
case object Bomb extends CellType
case object Empty extends CellType

