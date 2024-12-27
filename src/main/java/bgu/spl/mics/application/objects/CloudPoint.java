package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {

    private final double x;
    private final double y;

    /**
     * Constructs a CloudPoint in a 2D space.
     *
     * @param x The X-coordinate of the point
     * @param y The Y-coordinate of the point
     */
    public CloudPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

     /**
     * @return The X-coordinate of the point
     */
    public double getX(){
        return this.x;
    }

     /**
     * @return The Y-coordinate of the point
     */
    public double getY(){
        return this.y;
    }
}
