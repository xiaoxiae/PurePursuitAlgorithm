package main;

import processing.core.*;

import java.util.ArrayList;
import java.util.List;

public class PurePursuit extends PApplet {
    // List of the points
    private List<float[]> path;

    // List of a path drawn by the follower
    private List<float[]> followerPath;

    // A PathFollower object and its variables
    private PathFollower follower;
    private float followerSpeed = 2.5f;
    private float followerStopDistance = 2;

    private float followerSpeedDelta = 2.5f;

    //Size of the ellipses
    private float pointSize = 4;

    // The distance to look ahead (in pixels)
    private float lookaheadDistance = 45;

    // The colour of the pursued point
    private int[] pursuedCircleColor = new int[]{255, 0, 0};

    static public void main(String[] passedArgs) {
        PApplet.main(PurePursuit.class.getName());
    }

    @Override
    public void settings() {
        size(800, 400);
    }

    @Override
    public void setup() {
        colorMode(RGB);

        reset();
    }

    /**
     * Reset the simulation.
     */
    private void reset() {
        path = new ArrayList<>();

        follower = null;
        followerPath = null;
    }

    @Override
    public void draw() {
        background(255);

        // If there are any points on the follower's path
        if (followerPath != null) {
            // Iterate through all follower path points and draw them
            for (int i = 0; i < followerPath.size(); i+= 4) {

                // Coordinates of the point
                float[] pointCoords = followerPath.get(i);

                // If it isn't the first point, connect this point to its predecessor
                if (i > 0) {
                    // Coords of the previous point
                    float[] prevPointCoords = followerPath.get(i - 1);

                    // Draw the line
                    stroke(120);
                    line(pointCoords[0], pointCoords[1], prevPointCoords[0], prevPointCoords[1]);
                }
            }
        }

        // Iterate through all path points and draw them
        for (int i = 0; i < path.size(); i++) {
            // Coordinates of the point
            float[] pointCoords = path.get(i);

            // Create an eclipse as the point
            stroke(0);
            fill(0);
            ellipse(pointCoords[0], pointCoords[1], pointSize, pointSize);

            // If it isn't the first point, connect this point to its predecessor
            if (i > 0) {
                // Coords of the previous point
                float[] prevPointCoords = path.get(i - 1);

                // Draw the line
                line(pointCoords[0], pointCoords[1], prevPointCoords[0], prevPointCoords[1]);
            }
        }

        // If mouse was pressed, get the lookahead point
        if (mousePressed && mouseButton == LEFT) {
            int x = mouseX;
            int y = mouseY;

            // Get the lookahead point from the mouse coordinates
            float[] lookaheadPoint = getLookaheadPoint(x, y, lookaheadDistance);

            // If the function returned a valid point, draw it
            if (lookaheadPoint != null) drawLookaheadPoint(x, y, lookaheadPoint[0], lookaheadPoint[1]);
        }

        // Draw and potentially moves the PathFollower
        if (follower != null) {
            // Positions of the follower and its lookahead point
            float[] followerPosition = follower.getFollowerPosition();
            float[] lookaheadCoordinates = getLookaheadPoint(followerPosition[0], followerPosition[1], lookaheadDistance);

            // Draw the follower
            fill(0);
            ellipse(followerPosition[0], followerPosition[1], pointSize, pointSize);

            // If lookahead coordinates for the follower exist
            if (lookaheadCoordinates != null) {
                // To calculate the distance between the lookahead point and the follower
                double offsetLookaheadX = lookaheadCoordinates[0] - followerPosition[0];
                double offsetLookaheadY = lookaheadCoordinates[1] - followerPosition[1];

                // Draw the lookahead point
                drawLookaheadPoint(followerPosition[0], followerPosition[1], lookaheadCoordinates[0], lookaheadCoordinates[1]);

                // The distance from follower position to lookahead position
                float distance = 2 * (float)Math.sqrt(Math.pow(lookaheadCoordinates[0] - followerPosition[0], 2) + Math.pow(lookaheadCoordinates[1] - followerPosition[1], 2));

                // Circle around the follower
                noFill();
                ellipse(followerPosition[0], followerPosition[1], distance, distance);

                // If the follower reached the destination, delete the follower
                if (Math.sqrt(offsetLookaheadX * offsetLookaheadX + offsetLookaheadY * offsetLookaheadY) < followerStopDistance) {
                    follower = null;
                } else {
                    // Move the follower upon pressing 'f'
                    if (keyPressed && key == 'f') {
                        // We need to create a new coordinate pair, because the position of the pathFollower changes
                        float[] tempFollowerPosition = follower.getFollowerPosition();

                        // Add new point to the follower's path and move the follower
                        followerPath.add(new float[]{tempFollowerPosition[0], tempFollowerPosition[1]});
                        follower.moveFollowerTowardsPoint(lookaheadCoordinates[0], lookaheadCoordinates[1]);
                    }
                }
            }
        }
    }

    /**
     * Generate a lookahead point on the path.
     *
     * @param x The x of the origin.
     * @param y The y of the origin.
     * @return A float[] coordinate pair if the lookahead point exists, or null.
     */
    private float[] getLookaheadPoint(float x, float y, float lookaheadDistance) {
        // The point that will be selected to be pursued from the line segments
        float[] lookaheadPoint = new float[2];

        // Iterate through all the points
        for (int i = 0; i < path.size() - 1; i++) {
            // The path segment points
            float[] lineStartPoints = path.get(i);
            float[] lineEndPoints = path.get(i + 1);

            // Translated path segment and the mouse coordinates
            float[] translatedCoords = new float[]{lineEndPoints[0] - lineStartPoints[0], lineEndPoints[1] - lineStartPoints[1]};
            float[] translatedMouseCoords = new float[]{x - lineStartPoints[0], y - lineStartPoints[1]};

            // The angle to turn all coordinates by
            float angle = -(float) Math.atan2(translatedCoords[1], translatedCoords[0]);

            // Translated and rotated path segment and the mouse coordinates
            float[] turnedCoords = new float[]{translatedCoords[0] * (float) Math.cos(angle) - translatedCoords[1] * (float) Math.sin(angle), 0};
            float[] turnedMouseCoords = new float[]{translatedMouseCoords[0] * (float) Math.cos(angle) - translatedMouseCoords[1] * (float) Math.sin(angle), translatedMouseCoords[1] * (float) Math.cos(angle) + translatedMouseCoords[0] * (float) Math.sin(angle)};

            // The distance from the mouse's x coordinate above the line segment to the possible point on the line segment
            // Take note that if the segment is too far, the result of this operation wil be NaN (we can't create a  right triangle if a side is longer than a hypotenuse)
            float intersectingPointX = turnedMouseCoords[0] + (float) Math.sqrt(lookaheadDistance * lookaheadDistance - turnedMouseCoords[1] * turnedMouseCoords[1]);

            // If the point lays on the translated and rotated segment
            if ((intersectingPointX > 0) && (intersectingPointX < turnedCoords[0])) {
                lookaheadPoint[0] = intersectingPointX * cos(-angle) + lineStartPoints[0];
                lookaheadPoint[1] = intersectingPointX * sin(-angle) + lineStartPoints[1];
            }
        }

        // Do we even have any points to draw? If we do, attempt to do so.
        if (path.size() > 0) {
            // If the mouse is close enough to the end, simply select that as the pursuit target
            float[] endPointCoordinates = path.get(path.size() - 1);

            float endX = endPointCoordinates[0];
            float endY = endPointCoordinates[1];

            if (Math.sqrt((endX - x) * (endX - x) + (endY - y) * (endY - y)) <= lookaheadDistance) {
                lookaheadPoint[0] = endX;
                lookaheadPoint[1] = endY;
            }

            // If we selected any points to pursue, draw them.
            if (lookaheadPoint[0] != 0 && lookaheadPoint[1] != 0) {
                return new float[]{lookaheadPoint[0], lookaheadPoint[1]};
            }
        }

        return null;
    }

    /**
     * Draw a lookahead point and the line to it.
     *
     * @param x1 The x value of the follower.
     * @param y1 The y value of the follower.
     * @param x2 The x value of the lookahead.
     * @param y2 The y value of the lookahead.
     */
    private void drawLookaheadPoint(float x1, float y1, float x2, float y2) {
        // Line between object and lookahead point
        stroke(0);
        line(x1, y1, x2, y2);

        // Fill the circle with the desired color of the point to be pursued
        fill(pursuedCircleColor[0], pursuedCircleColor[1], pursuedCircleColor[2]);

        // Lookahead point
        ellipse(x2, y2, pointSize, pointSize);
    }

    @Override
    public void keyPressed() {
        //Reset the game
        if (key == 'r') reset();

        // Create a new follower object at the beginning of the path
        if (key == 'n' && path.size() > 0) {
            float[] firstPointCoordinates = path.get(0);

            followerPath = new ArrayList<>();
            follower = new PathFollower(firstPointCoordinates[0], firstPointCoordinates[1], followerSpeed);
        }

        // Increase lookahead distance
        if (key == '+') lookaheadDistance += followerSpeedDelta;

        // Decreased lookahead distance
        if (key == '-') lookaheadDistance -= followerSpeedDelta;
    }

    @Override
    public void mousePressed() {
        // Add a new path point
        if (mouseButton == RIGHT) {
            path.add(new float[]{mouseX, mouseY});
        }
    }
}