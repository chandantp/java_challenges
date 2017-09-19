Readme
======

- The application code is written using Scala
- Application input is from STDIN
- Application outputs to STDOUT
- The play area is hardcoded to 5 units x 5 units and is not configurable

Dependencies
------------
- Scala Build Tool (SBT)
- Scala compiler
- JDK 1.8

Compilation
-----------
From inside the robot root directory, run the following command to compile the app

    $ sbt compile

Test execution
--------------
From inside the robot root directory, run the following command to run test cases

    $ sbt test
    
Packaging
---------
From inside the robot root directory, run the following command to generate the application JAR.
This will generate the application JAR at `target/scala-<version>/robot_<version>.jar`
    
    $ sbt package

Running the App
---------------

From inside the robot root directory, run scala command and enter the input commands one command per line.
The end of input is signalled by Ctrl-D.
The output appears in the succeeding line(s).

    $ scala target/scala-2.12/robot_2.12-0.1.jar 
    PLACE 0,0,NORTH
    MOVE
    MOVE
    RIGHT
    MOVE
    MOVE
    REPORT
    ^D
    2,2,EAST

    $ scala target/scala-2.12/robot_2.12-0.1.jar
    PLACE 0,0,NORTH
    MOVE
    MOVE
    RIGHT
    MOVE
    MOVE
    REPORT
    MOVE
    MOVE
    REPORT
    ^D
    2,2,EAST
    4,2,EAST
