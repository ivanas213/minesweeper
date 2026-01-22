package model

sealed class GameStatus;
case object Won extends GameStatus
case object Lost extends GameStatus
case object Playing extends GameStatus