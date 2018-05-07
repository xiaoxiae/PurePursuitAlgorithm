package main;

import processing.core.*;

import java.util.ArrayList;
import java.util.List;

public class PurePursuit extends PApplet {
    // List of the points
    private List<float[]> points;

    // A PathFollower object and its variables
    private PathFollower pathFollower;
    private float pathFollowerSpeed = 2.5f;

    //Size of the ellipses
    private float pointSize = 4;

    // The distance to look ahead
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

        points = new ArrayList<>();
    }

    /**
     * Reset the simulation.
     */
    private void reset() {
        points = new ArrayList<>();
        pathFollower = null;
    }

    @Override
    public void draw() {
        background(255);

        // Iterate through all points and draw them
        for (int i = 0; i < points.size(); i++) {
            // Coordinates of the point
            float[] pointCoords = points.get(i);

            // Create an eclipse as the point
            fill(0);
            ellipse(pointCoords[0], pointCoords[1], pointSize, pointSize);

            // If it isn't the first point, connect this point to its predecessor
            if (i > 0) {
                // Coords of the previous point
                float[] prevPointCoords = points.get(i - 1);

                // Draw the line
                line(pointCoords[0], pointCoords[1], prevPointCoords[0], prevPointCoords[1]);
            }
        }

        // If mouse was pressed, get the lookahead point
        if (mousePressed && mouseButton == LEFT) {
            int x = mouseX;
            int y = mouseY;

            // Get the lookahead point from the mouse coordinates
            float[] lookaheadPoint = getLookaheadPoint(x, y);

            // If the function returned a valid point, draw it
            if (lookaheadPoint.length == 2) drawTwoPoints(x, y, lookaheadPoint[0], lookaheadPoint[1]);
        }

        // Draw and potentially moves the PathFollower
        if (pathFollower != null) {
            // Positions of the pathFollower and its lookahead point
            float[] followerPosition = pathFollower.getFollowerPosition();
            float[] lookaheadCoordinates = getLookaheadPoint(followerPosition[0], followerPosition[1]);

            // Move the follower upon pressing 'f'
            if (keyPressed && key == 'f')
                pathFollower.moveFollowerTowardsPoint(lookaheadCoordinates[0], lookaheadCoordinates[1]);

            // Draw the follower
            drawTwoPoints(followerPosition[0], followerPosition[1], lookaheadCoordinates[0], lookaheadCoordinates[1]);
        }
    }

    /**
     * Generate a lookahead point on the path.
     *
     * @param x The x of the origin.
     * @param y The y of the origin.
     * @return A float[] coordinate pair if the lookahead point exists, or an empty float[0] if it doesn't.
     */
    float[] getLookaheadPoint(float x, float y) {
        // The point that will be selected to be pursued from the line segments
        float[] lookaheadPoint = new float[2];

        // Iterate through all the points
        for (int i = 0; i < points.size() - 1; i++) {
            // The path segment points
            float[] lineStartPoints = points.get(i);
            float[] lineEndPoints = points.get(i + 1);

            // Translated path segment and the mouse coordinates
            float[] translatedCoords = new float[]{lineEndPoints[0] - lineStartPoints[0], lineEndPoints[1] - lineStartPoints[1]};
            float[] translatedMouseCoords = new float[]{x - lineStartPoints[0], y - lineStartPoints[1]};

            // The angle to turn all coordinates by
            // Since atan only works in quadrants I and IV, some additional calculation must be made
            float angle = -(float) Math.atan(translatedCoords[1] / translatedCoords[0]);
            if (translatedCoords[1] < 0 && translatedCoords[0] < 0) angle += PI;
            else if (translatedCoords[0] < 0) angle -= PI;

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
        if (points.size() > 0) {
            // If the mouse is close enough to the end, simply select that as the pursuit target
            float[] endPointCoordinates = points.get(points.size() - 1);

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

        return new float[0];
    }

    /**
     * Draw two points and a line between them.
     *
     * @param x1 The x value of the first point.
     * @param y1 The y value of the first point.
     * @param x2 The x value of the second point.
     * @param y2 The y value of the second point.
     */
    private void drawTwoPoints(float x1, float y1, float x2, float y2) {
        // Line between object and lookahead point
        line(x1, y1, x2, y2);

        // Object point
        ellipse(x1, y1, pointSize, pointSize);

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
        if (key == 'n' && points.size() > 0) {
            float[] firstPointCoordinates = points.get(0);

            pathFollower = new PathFollower(firstPointCoordinates[0], firstPointCoordinates[1], pathFollowerSpeed);
        }
    }

    @Override
    public void mousePressed() {
        // Add a new path point
        if (mouseButton == RIGHT) {
            points.add(new float[]{mouseX, mouseY});
        }
    }
}