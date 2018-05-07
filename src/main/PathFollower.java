package main;

class PathFollower {
    // Coordinates of the follower.
    private float[] position;

    private float speed;

    PathFollower(float x, float y, float speed) {
        this.position = new float[]{x, y};

        this.speed = speed;
    }

    /**
     * Moves the follower towards a point at the follower's speed.
     *
     * @param xToMoveTowards The x value of the coordinate towards which to move.
     * @param yToMoveTowards The y value of the coordinate towards which to move.
     */
    void moveFollowerTowardsPoint(float xToMoveTowards, float yToMoveTowards) {
        // Offset the point
        float offsetX = xToMoveTowards - position[0];
        float offsetY = yToMoveTowards - position[1];

        // Normalize the vector
        float lengthToPoint = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
        float normalizedPointX = offsetX / lengthToPoint;
        float normalizedPointY = offsetY / lengthToPoint;

        // Move towards the point at a certain speed
        position[0] += normalizedPointX * speed;
        position[1] += normalizedPointY * speed;
    }

    /**
     * Returns the current coordinates of the follower.
     *
     * @return A float[2], with arr[0] being the x value and arr[1] being the y value.
     */
    float[] getFollowerPosition() {
        return position;
    }
}
