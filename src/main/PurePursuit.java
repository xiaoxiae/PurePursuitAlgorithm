package main;

import processing.core.*;

import java.util.ArrayList;
import java.util.List;

public class PurePursuit extends PApplet {
    // List of the points
    private List<float[]> points;

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

    private void reset() {
        points = new ArrayList<>();
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

        float[] lookaheadPoint = getLookaheadPoint();

        if (lookaheadPoint.length == 2) {
            line(mouseX, mouseY, lookaheadPoint[0], lookaheadPoint[1]);
            ellipse(lookaheadPoint[0], lookaheadPoint[1], pointSize, pointSize);
        }
    }

    float[] getLookaheadPoint() {
        // The point that will be selected to be pursued from the line segments
        float[] lookaheadPoint = new float[2];

        // If the mouse left-clicked, draw the line lookahead line
        if (mousePressed && mouseButton == LEFT) {
            // Iterate through all the points
            for (int i = 0; i < points.size() - 1; i++) {
                // The path segment points
                float[] lineStartPoints = points.get(i);
                float[] lineEndPoints = points.get(i + 1);

                // Translated path segment and the mouse coordinates
                float[] translatedCoords = new float[]{lineEndPoints[0] - lineStartPoints[0], lineEndPoints[1] - lineStartPoints[1]};
                float[] translatedMouseCoords = new float[]{mouseX - lineStartPoints[0], mouseY - lineStartPoints[1]};

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
                // Fill the circle with the desired color of the point to be pursued
                fill(pursuedCircleColor[0], pursuedCircleColor[1], pursuedCircleColor[2]);

                // If the mouse is close enough to the end, simply select that as the pursuit target
                float[] endPointCoordinates = points.get(points.size() - 1);

                float x = endPointCoordinates[0];
                float y = endPointCoordinates[1];

                if (Math.sqrt((x - mouseX) * (x - mouseX) + (y - mouseY) * (y - mouseY)) <= lookaheadDistance) {
                    lookaheadPoint[0] = x;
                    lookaheadPoint[1] = y;
                }

                // If we selected any points to pursue, draw them.
                if (lookaheadPoint[0] != 0 && lookaheadPoint[1] != 0) {
                    return new float[]{lookaheadPoint[0], lookaheadPoint[1]};
                }
            }
        }

        return new float[0];
    }

    @Override
    public void keyPressed() {
        if (key == 'r') reset();
    }

    @Override
    public void mousePressed() {
        if (mouseButton == RIGHT) {
            points.add(new float[]{mouseX, mouseY});
        }
    }
}