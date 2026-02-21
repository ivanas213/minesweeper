package model

enum MoveType:
  case Left, Right

case class Move(row: Int, col: Int, moveType: MoveType)
