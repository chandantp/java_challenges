package com.chandantp.robot

import Robot._
import Arena.{MaxX, MaxY, MinX, MinY}

import scala.util.{Failure, Success, Try}

object Robot {
  val EmptyString = ""
  val Comma = ","

  // Command strings
  val Place = "PLACE"
  val Move = "MOVE"
  val Left = "LEFT"
  val Right = "RIGHT"
  val Report = "REPORT"

  val East = "EAST"
  val West = "WEST"
  val North = "NORTH"
  val South = "SOUTH"

  def apply(position: Option[Position]) = new Robot(position)


  // Invalid arguments or argument length are ignored
  def parse(position: String): Option[Position] = {
    val tokens = position.toUpperCase.split(Comma)
    Try((tokens(0).trim.toInt, tokens(1).trim.toInt, tokens(2).trim)) match {
      case Success((x, y, f)) => parse(x, y, f)
      case Failure(_) => None
    }
  }

  // Positions with invalid direction are ignored
  def parse(x: Int, y: Int, f: String): Option[Position] = {
    if (x >= MinX && x <= MaxX && y >= MinY && y <= MaxY &&
      (f == East || f == West || f == North || f == South)) {
      Option(new Position(x, y, f))
    }
    else None
  }
}

class Robot(position: Option[Position]) {

  def this() = this(None)

  def place(command: String): Robot = {
    Robot(parse(command.replace(Robot.Place, Robot.EmptyString)))
  }

  def move: Robot = position match {
    case Some(pos) => {
      val newPos = pos.facing match {
        case East => parse(pos.x+1, pos.y, pos.facing)
        case North => parse(pos.x, pos.y+1, pos.facing)
        case West => parse(pos.x-1, pos.y, pos.facing)
        case South => parse(pos.x, pos.y-1, pos.facing)
      }
      Robot(if (newPos != None) newPos else position)
    }
    case None => new Robot
  }

  def left: Robot = position match {
    case Some(pos) => Robot(pos.facing match {
      case East => parse(pos.x, pos.y, North)
      case North => parse(pos.x, pos.y, West)
      case West => parse(pos.x, pos.y, South)
      case South => parse(pos.x, pos.y, East)
    })
    case None => new Robot
  }

  def right: Robot = position match {
    case Some(pos) => Robot(pos.facing match {
      case East => parse(pos.x, pos.y, South)
      case South => parse(pos.x, pos.y, West)
      case West => parse(pos.x, pos.y, North)
      case North => parse(pos.x, pos.y, East)
    })
    case None => new Robot
  }

  def report: Option[String] = position.map(_.toString)

}
