package bgu.spl.mics.application.objects;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate system.
 */
public class Pose {
    private final int time;
    private final float x;
    private final float y;
    private final float yaw;
    
    /**
     * Constructor to initialize the pose.
     * 
     * @param x    The X-coordinate of the pose
     * @param y    The Y-coordinate of the pose
     * @param yaw  The yaw angle (orientation) of the pose
     * @param time The timestamp for the pose
     */
    public Pose(int time, float x, float y, float yaw) {
        this.time = time;
        this.x = x;
        this.y = y;
        this.yaw = yaw;
    }

    /**
     * @return The X-coordinate of the pose
     */
    public float getX() {
        return x;
    }

    /**
     * @return The Y-coordinate of the pose
     */
    public float getY() {
        return y;
    }

    /**
     * @return The yaw angle of the pose
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * @return The timestamp for the pose
     */
    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Pose{" +
                "x=" + x +
                ", y=" + y +
                ", yaw=" + yaw +
                ", time=" + time +
                '}';
    }
}
