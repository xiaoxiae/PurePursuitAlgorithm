# Pure Pursuit Algorithm

## Introduction
This repository is a Processing implementation of the Adaptive Pure Pursuit algorithm used to control FRC robots.

![](https://i.imgur.com/CKeM8nW.gif)

## Controls and Features
The program features minimalistic path creation and following functionality:

* **Left click** shows the lookahead line from the cursor to the nearest path line. 
* **Right click** creates new points on the path.
* **r** resets the simulation.
* **n** creates a new path follower that is moved by pressing.
    + **f** moves the follower towards the end of the path.
	+ **+** increases the lookahead distance.
	+ **-** decreases the lookahead distance.
	
The follower also leaves a dashed path behind and has a circle around its origin to visualise the lookahead distance.

## Resources
* [A Feedforward Control Approach to the Local Navigation Problem for Autonomous Vehicles](https://www.ri.cmu.edu/pub_files/pub1/kelly_alonzo_1994_4/kelly_alonzo_1994_4.pdf) - the main resource used in the implementation of this project.
* [Pure Pursuit Controller - MATLAB & Simulink](https://www.mathworks.com/help/robotics/ug/pure-pursuit-controller.html) - a neat Mathworks article about the Pure Pursuit Controller.
* [254's 2017 code](https://github.com/Team254/FRC-2017-Public) - their commented code has has been a great resource and provided a lot of good pointers for various quirks in the implementation of the algorithm.
* [Processing 3](https://processing.org/download/) - the graphic library used in this project.