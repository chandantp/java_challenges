package com.chandantp.robot

import org.scalatest.FunSuite

class PositionTest extends FunSuite {

  test("parsing an invalid Position value \"ABC,HELLO,NORTH\" evaluates to None") {
    assert(Position("ABC,HELLO,NORTH") === None)
  }

  test("parsing an invalid Position value \"1,1\" evaluates to None") {
    assert(Position("1,1") === None)
  }

  test("parsing an invalid Position value \"1,1,ABC\" evaluates to None") {
    assert(Position("1,1,ABC") === None)
  }

  test("parsing a valid Position value \"  1, 1, NORTH \" is successful") {
    assert(Position("  1, 1, NORTH ") === Position(1,1,"NORTH") && Position(1,1,"NORTH") != None)
  }

  test("parsing a valid Position value \" 1,1,north\" is successful") {
    assert(Position("1,1,north") === Position(1,1,"NORTH") && Position(1,1,"NORTH") != None)
  }

  test("parsing a valid Position value \"1,1,NORTH\" is successful") {
    assert(Position("1,1,NORTH") === Position(1,1,"NORTH") && Position(1,1,"NORTH") != None)
  }

  test("parsing a valid Position value \"  1, 1, EAST\" is successful") {
    assert(Position("  1, 1, EAST ") === Position(1,1,"EAST") && Position(1,1,"EAST") != None)
  }

  test("parsing a valid Position value \"  1, 1, SOUTH\" is successful") {
    assert(Position("  1, 1, SOUTH ") === Position(1,1,"SOUTH") && Position(1,1,"SOUTH") != None)
  }

  test("parsing a valid Position value \"  1, 1, WEST\" is successful") {
    assert(Position("  1, 1, WEST ") === Position(1,1,"WEST") && Position(1,1,"WEST") != None)
  }

}
