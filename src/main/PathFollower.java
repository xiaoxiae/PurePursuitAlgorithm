package main;

class PathFollower {
    // follower coordinates
    private float[] position;

    // follower speed
    private float speed;

    PathFollower(float x, float y, float speed) {
        this.position = new float[]{x, y};
        this.speed = speed;
    }

    /**
     * Moves the follower towards a point by the follower's speed.
     *
     * @param x The x value of the coordinate towards which to move.
     * @param y The y value of the coordinate towards which to move.
     */
    void moveFollowerTowardsPoint(float x, float y) {
        // move the point to origin (the follower's coordinates)
        float offsetX = x - position[0];
        float offsetY = y - position[1];

        // normalize the vector
        float distanceToPoint = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
        float normalizedX = offsetX / distanceToPoint;
        float normalizedY = offsetY / distanceToPoint;

        // move towards the point at a certain speed
        position[0] += normalizedX * speed;
        position[1] += normalizedY * speed;
    }

    /**
     * Returns the coordinates of the follower.
     *
     * @return A float[2], with arr[0] being the x value and arr[1] being the y value.
     */
    float[] getFollowerPosition() {
        return position;
    }
}
