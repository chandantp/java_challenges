package com.chandantp.robot

import scala.util.{Failure, Success, Try}

case class Position(val x: Int, val y: Int, val facing: String) {
  override def toString = "%d,%d,%s".format(x, y, facing)
}

object Position {
  val MinX = 0
  val MinY = 0
  val MaxX = 4
  val MaxY = 4
  val Comma = ","

  // Invalid arguments or argument length are ignored
  def apply(position: String): Option[Position] = {
    val tokens = position.toUpperCase.split(Comma)
    Try((tokens(0).trim.toInt, tokens(1).trim.toInt, tokens(2).trim)) match {
      case Success((x, y, f)) => Position(x, y, f)
      case Failure(_) => None
    }
  }

  // Positions with invalid direction are ignored
  def apply(x: Int, y: Int, facing: String): Option[Position] = {
    import Robot._
    if (x >= MinX && x <= MaxX && y >= MinY && y <= MaxY &&
      (facing == East || facing == West || facing == North || facing == South)) {
      Option(new Position(x, y, facing))
    }
    else None
  }
}
