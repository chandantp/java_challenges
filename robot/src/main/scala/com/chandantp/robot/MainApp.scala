package com.chandantp.robot

object MainApp {

  def main(args: Array[String]): Unit = {
    io.Source.stdin.getLines.toList.foldLeft(new Robot)((robot,command) => robot.execute(command))
  }

}
