package model

sealed trait Cell
case object Mine extends Cell
case class Number(value: Int) extends Cell
