package com.chandantp.robot

object Robot {
  val EmptyString = ""

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

}

class Robot(position: Option[Position]) {

  def this() = this(None)
  import Robot._

  def execute(command: String): Robot = command.trim.toUpperCase match {
    case cmd: String if cmd.trim.startsWith(Place) => place(cmd)
    case Move => move
    case Left => left
    case Right => right
    case Report => { report.foreach(println); this }
    case _ => this
  }

  def place(command: String): Robot = {
    Robot(Position(command.replace(Robot.Place, Robot.EmptyString)))
  }


  def move: Robot = position match {
    case Some(pos) => {
      val newPos = pos.facing match {
        case East => Position(pos.x+1, pos.y, pos.facing)
        case North => Position(pos.x, pos.y+1, pos.facing)
        case West => Position(pos.x-1, pos.y, pos.facing)
        case South => Position(pos.x, pos.y-1, pos.facing)
      }
      Robot(if (newPos != None) newPos else position)
    }
    case None => new Robot
  }

  def left: Robot = position match {
    case Some(pos) => Robot(pos.facing match {
      case East => Position(pos.x, pos.y, North)
      case North => Position(pos.x, pos.y, West)
      case West => Position(pos.x, pos.y, South)
      case South => Position(pos.x, pos.y, East)
    })
    case None => new Robot
  }

  def right: Robot = position match {
    case Some(pos) => Robot(pos.facing match {
      case East => Position(pos.x, pos.y, South)
      case South => Position(pos.x, pos.y, West)
      case West => Position(pos.x, pos.y, North)
      case North => Position(pos.x, pos.y, East)
    })
    case None => new Robot
  }

  def report: Option[String] = position.map(_.toString)

}
