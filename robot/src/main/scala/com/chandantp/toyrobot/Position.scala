package com.chandantp.robot

case class Position(val x: Int, val y: Int, val facing: String) {
  override def toString = "%d,%d,%s".format(x, y, facing)
}
