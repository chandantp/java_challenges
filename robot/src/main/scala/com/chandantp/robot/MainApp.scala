package com.chandantp.robot

object MainApp {

  def main(args: Array[String]): Unit = {
    val input = io.Source.stdin.getLines.toList
    var robot = new Robot
    input.foreach(command => robot = robot.execute(command))
  }

}

