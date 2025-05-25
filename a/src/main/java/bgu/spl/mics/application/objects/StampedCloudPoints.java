package bgu.spl.mics.application.objects;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
import java.util.List;

/**
 * Represents a group of cloud points corresponding to an ID with a timestamp.
 */
public class StampedCloudPoints {
    
    private final String id; // The ID of the object
    private final int time; // The time the object was tracked
    private final List<List<Double>> cloudPoints; // List of cloud points

    /**
     * Constructor to initialize all fields.
     *
     * @param id          The ID of the object.
     * @param time        The time the object was tracked.
     * @param cloudPoints List of cloud points.
     */
    public StampedCloudPoints(String id, int time, List<List<Double>> cloudPoints) {
        this.id = id;
        this.time = time;
        this.cloudPoints = cloudPoints;
    }
/**
     * @return The ID of the object.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The timestamp when the object was tracked.
     */
    public int getTime() {
        return time;
    }

    /**
     * @return The list of cloud points associated with the object.
     */
    public List<List<Double>> getCloudPoints() {
        return cloudPoints;
    }

    /**
     * Returns a string representation of the StampedCloudPoints object.
     *
     * @return A string representation of the StampedCloudPoints object.
     */
    @Override
    public String toString() {
        return "StampedCloudPoints{" +
                "id='" + id + '\'' +
                ", time=" + time +
                ", cloudPoints=" + cloudPoints +
                '}';
    }
}
