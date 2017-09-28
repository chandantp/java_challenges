package com.chandantp.robot

import org.scalatest.{BeforeAndAfter, FunSuite}

class RobotTest extends FunSuite {

  val robot: Robot = new Robot

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

  test("\"PLACE 1,1,NORTH\" command is successful") {
    assert(robot.place("PLACE 1,1,NORTH").report  === Some("1,1,NORTH"))
  }

  test("\"PLACE 10,1,NORTH\" command with invalid position is ignored") {
    assert(robot.place("PLACE 10,1,NORTH").report === None)
  }

  test("\"PLACE 2,10,NORTH\" command with invalid position is ignored") {
    assert(robot.place("PLACE 2,10,NORTH").report === None)
  }

  test("\"PLACE -1,10,NORTH\" command with invalid position is ignored") {
    assert(robot.place("PLACE -1,10,NORTH").report === None)
  }

  test("\"PLACE 3,-3,NORTH\" command with invalid position is ignored") {
    assert(robot.place("PLACE 3,-3,NORTH").report === None)
  }

  test("\"PLACE 1,1,ABC\" command with invalid direction value is ignored") {
    assert(robot.place("PLACE 1,1,ABC").report === None)
  }

  test("\"PLACE ABC,1,NORTH\" command with invalid 'x' coordinate fails") {
    assert(robot.place("PLACE ABC,1,NORTH").report === None)
  }

  test("\"PLACE 1,ABC,NORTH\" command with invalid 'y' coordinate fails") {
    assert(robot.place("PLACE 1,ABC,NORTH").report === None)
  }

  test("\"LEFT\" without prior successful PLACE command exec is ignored") {
    assert(robot.left.report === None)
  }

  test("\"LEFT\" with prior successful PLACE command exec is successful") {
    assert(robot.place("PLACE 0,0,NORTH").left.report === Some("0,0,WEST"))
    assert(robot.place("PLACE 0,0,NORTH").left.left.report === Some("0,0,SOUTH"))
    assert(robot.place("PLACE 0,0,NORTH").left.left.left.report === Some("0,0,EAST"))
    assert(robot.place("PLACE 0,0,NORTH").left.left.left.left.report === Some("0,0,NORTH"))
  }

  test("\"RIGHT\" without prior successful PLACE command exec is successful") {
    assert(robot.right.report === None)
  }

  test("\"RIGHT\" without prior successful PLACE command exec is ignored") {
    assert(robot.place("PLACE 0,0,NORTH").right.report === Some("0,0,EAST"))
    assert(robot.place("PLACE 0,0,NORTH").right.right.report === Some("0,0,SOUTH"))
    assert(robot.place("PLACE 0,0,NORTH").right.right.right.report === Some("0,0,WEST"))
    assert(robot.place("PLACE 0,0,NORTH").right.right.right.right.report === Some("0,0,NORTH"))
  }

  test("\"MOVE\" without prior successful PLACE command exec is ignored") {
    assert(robot.move.report === None)
  }

  test("\"MOVE\" without prior successful PLACE command exec is successful") {
    assert(robot.place("PLACE 0,0,NORTH").move.report === Some("0,1,NORTH"))
  }

  test("\"MOVE\" north-south-east-west is successful") {
    assert(robot.place("PLACE 0,0,NORTH").move.report === Some("0,1,NORTH"))
    assert(robot.place("PLACE 0,0,NORTH").move.right.move.report === Some("1,1,EAST"))
    assert(robot.place("PLACE 0,0,NORTH").move.right.move.right.move.report === Some("1,0,SOUTH"))
    assert(robot.place("PLACE 0,0,NORTH").move.right.move.right.move.right.move.report === Some("0,0,WEST"))
  }

  test("\"MOVE\" out of arena commands are ignored") {
    assert(robot.place("PLACE 0,0,SOUTH").move.report === Some("0,0,SOUTH"))
    assert(robot.place("PLACE 0,0,WEST").move.report === Some("0,0,WEST"))
    assert(robot.place("PLACE 4,4,NORTH").move.report === Some("4,4,NORTH"))
    assert(robot.place("PLACE 4,4,EAST").move.report === Some("4,4,EAST"))
  }

  test("GAME play-1 successful") {
    assert(robot.place("PLACE 1,2,EAST").move.move.left.move.report === Some("3,3,NORTH"))
  }

  test("GAME play-2 successful") {
    assert(robot.move.move.left.move.move.place("PLACE 10,-1,EAST").report === None)
    assert(robot.move.move.left.move.move.place("PLACE 10,-1,EAST").move.move.left.move.move.report === None)
    assert(robot.place("PLACE 0,0,NORTH").move.right.move.left.move.right.move.left.move.right.move.left.move.right.move.left.report === Some("4,4,NORTH"))
  }

  test("GAME play-3 successful with execute() helper method") {
    assert(robot
      .execute("PLACE 1,2,EAST").execute("MOVE").execute("MOVE").execute("blah")
      .execute("LEFT").execute("MOVE").report === Some("3,3,NORTH"))
  }

  test("GAME play-4 successful with execute() helper method") {
    assert(robot
      .execute("move").execute("blah").execute("move").execute("left").execute("move")
      .execute("move").execute("PLACE 10,-1,EAST").report === None)
    assert(robot
      .execute("move").execute("").execute("move").execute("left").execute("move")
      .execute("move").execute("PLACE 10,-1,EAST").execute("move").execute("move")
      .execute("left").execute("move").execute("move").report === None)
    assert(robot
      .execute("PLACE 0,0,NORTH").execute("move").execute("bla").execute("right").execute("move")
      .execute("left").execute("move").execute("right").execute("move").execute("left")
      .execute("move").execute("right").execute("move").execute("left").execute("move")
      .execute("right").execute("move").execute("left").report === Some("4,4,NORTH"))
  }
}
