# Pure Pursuit Algorithm

## Introduction
This repository is a Processing implementation of the Adaptive Pure Pursuit algorithm used to control FRC robots.

![](https://i.imgur.com/tBCkt62.gif)

## Controls
The program features minimalistic path creation, and path following functionality:

* **Left click** shows the lookahead line from the cursor to the nearest path line. 
* **Right click** creates new points on the path.
* **r** resets the simulation.
* **n** creates a new path follower that is moved by pressing.
    + **f** moves the follower towards the end of the path.

## Running the project

### Processing in an IDE
The simplest way to run the project is to create a new project from existing sources using the IDE of your choice and adding the core processing library.

1. Get core.jar from the Processing library. This can be done by downloading [Processing](https://processing.org/download). The JAR can be found in `/core/library/core.jar`.
2. Create a new processing [Intellij](https://stackoverflow.com/questions/36765288/how-to-use-processing-3-on-intellij-idea) or [Eclipse](https://processing.org/tutorials/eclipse/) project from existing sources.
3. Add the `core.jar` library to your Eclipse/Intellij project.
4. Run the project!

## Resources
* [A Feedforward Control Approach to the Local Navigation Problem for Autonomous Vehicles](https://www.ri.cmu.edu/pub_files/pub1/kelly_alonzo_1994_4/kelly_alonzo_1994_4.pdf) - the main resource used in the implementation of this project.
* [Pure Pursuit Controller - MATLAB & Simulink](https://www.mathworks.com/help/robotics/ug/pure-pursuit-controller.html) - a neat Mathworks article about the Pure Pursuit Controller.
* [254's 2017 code](https://github.com/Team254/FRC-2017-Public) - their commented code has has been a great resource and provided a lot of good pointers for various quirks in the implementation of the algorithm.
* [Processing 3](https://processing.org/download/) - the graphic library used in this project.