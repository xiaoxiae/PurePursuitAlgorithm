# Pure Pursuit Algorithm

## Introduction
This repository is a Processing implementation of the Adaptive Pure Pursuit algorithm used to control FRC robots.

![](https://i.imgur.com/CKeM8nW.gif)

## Controls and Features
The program offers the following functionality:

* **Left click** shows the lookahead line from the cursor to the nearest path segment.
* **Right click** creates new points of the path.
* **r** resets the simulation.
* **n** creates a new path follower that is moved by pressing **f**.
- **+** increases the lookahead distance.
- **-** decreases the lookahead distance.

## Resources
* [A Feedforward Control Approach to the Local Navigation Problem for Autonomous Vehicles](https://www.ri.cmu.edu/pub_files/pub1/kelly_alonzo_1994_4/kelly_alonzo_1994_4.pdf) - the main resource used in the implementation of this project.
* [Pure Pursuit Controller - MATLAB & Simulink](https://www.mathworks.com/help/robotics/ug/pure-pursuit-controller.html) - a useful MathWorks article about the Pure Pursuit Controller.
* [254's 2017 code](https://github.com/Team254/FRC-2017-Public) - a real-world implementation of the controller.
* [Processing 3](https://processing.org/download/) - the graphic library used in this project.