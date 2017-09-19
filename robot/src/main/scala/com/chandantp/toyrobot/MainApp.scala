package com.chandantp.robot

import Robot._

object MainApp {

  def main(args: Array[String]): Unit = {
    val input = io.Source.stdin.getLines.toList
    var robot = new Robot

    input.foreach(command => command.trim.toUpperCase match {
      case cmd: String if cmd.trim.startsWith(Place) => robot = robot.place(cmd)
      case Move => robot = robot.move
      case Left => robot = robot.left
      case Right => robot = robot.right
      case Report => robot.report.foreach(println)
    })
  }

}

