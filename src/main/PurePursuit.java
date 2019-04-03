package main;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PurePursuit extends PApplet {
    // list of the points of the path
    private List<float[]> path;

    // list of the points of the path drawn by the follower
    private List<float[]> followerPath;

    // a PathFollower object and its variables
    private PathFollower follower;
    private float followerSpeed = 2.5f;
    private float followerStopDistance = 2;

    // the value by which the stop distance changes
    private float lookaheadDistanceDelta = 2.5f;

    // size of the points
    private float pointSize = 4;

    // the lookahead distance
    private float lookaheadDistance = 45;

    // the color of the pursued point
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
        reset();
    }

    /**
     * Resets the simulation by setting the follower and all of the paths to equal null.
     */
    private void reset() {
        path = new ArrayList<>();

        follower = null;
        followerPath = null;
    }

    @Override
    public void draw() {
        background(255);

        // draw the follower's path
        if (followerPath != null) {
            // skip some of the lines (increment by more than 1) to make the line seem dotted
            for (int i = 0; i < followerPath.size() - 1; i += 4) {

                float[] p1 = followerPath.get(i);
                float[] p2 = followerPath.get(i + 1);

                stroke(120);
                line(p1[0], p1[1], p2[0], p2[1]);
            }
        }

        // draw the path
        for (int i = 0; i < path.size(); i++) {
            float[] point = path.get(i);

            // create an ellipse to symbolize a point
            stroke(0);
            fill(0);
            ellipse(point[0], point[1], pointSize, pointSize);

            // if it isn't the first point, connect it to its predecessor
            if (i > 0) {
                float[] previousPoint = path.get(i - 1);

                stroke(0);
                line(point[0], point[1], previousPoint[0], previousPoint[1]);
            }
        }

        // if mouse was pressed, attempt to draw the lookahead point from the mouse coords
        if (mousePressed && mouseButton == LEFT) {
            int x = mouseX;
            int y = mouseY;

            float[] lookaheadPoint = getLookaheadPoint(x, y, lookaheadDistance);

            // if the point is valid, draw it
            if (lookaheadPoint != null) {
                drawLookaheadPoint(x, y, lookaheadPoint[0], lookaheadPoint[1]);
            }
        }

        // draw and potentially move the PathFollower
        if (follower != null) {
            float[] position = follower.getFollowerPosition();
            float[] lookahead = getLookaheadPoint(position[0], position[1], lookaheadDistance);

            // draw the follower
            fill(0);
            ellipse(position[0], position[1], pointSize, pointSize);

            // if lookahead exists
            if (lookahead != null) {
                // draw the lookahead point
                drawLookaheadPoint(position[0], position[1], lookahead[0], lookahead[1]);

                // calculate the distance to the lookahead point
                double deltaX = lookahead[0] - position[0];
                double deltaY = lookahead[1] - position[1];
                float distance = 2 * (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

                // draw the circle around the follower
                noFill();
                ellipse(position[0], position[1], distance, distance);

                // if the follower reached the destination, delete the follower
                if (distance < followerStopDistance) {
                    follower = null;
                } else {
                    // move the follower upon pressing 'f'
                    if (keyPressed && key == 'f') {
                        // add the follower's current position to its path
                        float[] followerPosition = follower.getFollowerPosition();
                        followerPath.add(new float[]{followerPosition[0], followerPosition[1]});

                        // move it
                        follower.moveFollowerTowardsPoint(lookahead[0], lookahead[1]);
                    }
                }
            }
        }
    }

    /**
     * Returns the sign of the input number n. Note that the function returns 1 for n = 0 to satisfy the requirements
     * set forth by the line-circle intersection formula.
     *
     * @param n The number to return the sign of.
     * @return A float value of the sign of the number (-1.0f for n < 0, else 1.0f).
     */
    private float signum(float n) {
        if (n == 0) return 1;
        else return Math.signum(n);
    }

    /**
     * Generate the furthest lookahead point on the path that is distance r from the point (x, y).
     *
     * @param x The x of the origin.
     * @param y The y of the origin.
     * @param r The lookahead distance.
     * @return A float[] coordinate pair if the lookahead point exists, or null.
     * @see <a href="http://mathworld.wolfram.com/Circle-LineIntersection.html">Circle-Line Intersection</a>
     */
    private float[] getLookaheadPoint(float x, float y, float r) {
        float[] lookahead = null;

        // iterate through all pairs of points
        for (int i = 0; i < path.size() - 1; i++) {
            // form a segment from each two adjacent points
            float[] segmentStart = path.get(i);
            float[] segmentEnd = path.get(i + 1);

            // translate the segment to the origin
            float[] p1 = new float[]{segmentStart[0] - x, segmentStart[1] - y};
            float[] p2 = new float[]{segmentEnd[0] - x, segmentEnd[1] - y};

            // calculate an intersection of a segment and a circle with radius r (lookahead) and origin (0, 0)
            float dx = p2[0] - p1[0];
            float dy = p2[1] - p1[1];
            float d = (float) Math.sqrt(dx * dx + dy * dy);
            float D = p1[0] * p2[1] - p2[0] * p1[1];

            // if the discriminant is zero or the points are equal, there is no intersection
            float discriminant = r * r * d * d - D * D;
            if (discriminant < 0 || Arrays.equals(p1, p2)) continue;

            // the x components of the intersecting points
            float x1 = (float) (D * dy + signum(dy) * dx * Math.sqrt(discriminant)) / (d * d);
            float x2 = (float) (D * dy - signum(dy) * dx * Math.sqrt(discriminant)) / (d * d);

            // the y components of the intersecting points
            float y1 = (float) (-D * dx + Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);
            float y2 = (float) (-D * dx - Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);

            // whether each of the intersections are within the segment (and not the entire line)
            boolean validIntersection1 = Math.min(p1[0], p2[0]) < x1 && x1 < Math.max(p1[0], p2[0])
                    || Math.min(p1[1], p2[1]) < y1 && y1 < Math.max(p1[1], p2[1]);
            boolean validIntersection2 = Math.min(p1[0], p2[0]) < x2 && x2 < Math.max(p1[0], p2[0])
                    || Math.min(p1[1], p2[1]) < y2 && y2 < Math.max(p1[1], p2[1]);

            // remove the old lookahead if either of the points will be selected as the lookahead
            if (validIntersection1 || validIntersection2) lookahead = null;

            // select the first one if it's valid
            if (validIntersection1) {
                lookahead = new float[]{x1 + x, y1 + y};
            }

            // select the second one if it's valid and either lookahead is none,
            // or it's closer to the end of the segment than the first intersection
            if (validIntersection2) {
                if (lookahead == null || Math.abs(x1 - p2[0]) > Math.abs(x2 - p2[0]) || Math.abs(y1 - p2[1]) > Math.abs(y2 - p2[1])) {
                    lookahead = new float[]{x2 + x, y2 + y};
                }
            }
        }

        // special case for the very last point on the path
        if (path.size() > 0) {
            float[] lastPoint = path.get(path.size() - 1);

            float endX = lastPoint[0];
            float endY = lastPoint[1];

            // if we are closer than lookahead distance to the end, set it as the lookahead
            if (Math.sqrt((endX - x) * (endX - x) + (endY - y) * (endY - y)) <= r) {
                return new float[]{endX, endY};
            }
        }

        return lookahead;
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
        // line between object and lookahead point
        stroke(0);
        line(x1, y1, x2, y2);

        // fill the circle with the desired color of the point to be pursued
        fill(pursuedCircleColor[0], pursuedCircleColor[1], pursuedCircleColor[2]);

        // lookahead point
        ellipse(x2, y2, pointSize, pointSize);
    }

    /**
     * Is called when a key is pressed; controls most of the program controls.
     */
    @Override
    public void keyPressed() {
        switch (key) {
            case 'r':
                reset();
                break;
            case 'n':
                if (path.size() > 0) {
                    float[] firstPointCoordinates = path.get(0);

                    followerPath = new ArrayList<>();
                    follower = new PathFollower(firstPointCoordinates[0], firstPointCoordinates[1], followerSpeed);
                }
                break;
            case '+':
                lookaheadDistance += lookaheadDistanceDelta;
                break;
            case '-':
                lookaheadDistance -= lookaheadDistanceDelta;
                break;
        }
    }

    /**
     * Is called when the mouse is pressed; creates new points.
     */
    @Override
    public void mousePressed() {
        // add a new path point upon pressing the right button
        if (mouseButton == RIGHT) {
            path.add(new float[]{mouseX, mouseY});
        }
    }
}