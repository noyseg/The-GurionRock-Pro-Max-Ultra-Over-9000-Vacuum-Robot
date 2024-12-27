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
    
    private String id; // The ID of the object
    private int time; // The time the object was tracked
    private List<List<Double>> cloudPoints; // List of cloud points

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

    // Getter for id
    public String getId() {
        return id;
    }

    // Setter for id
    public void setId(String id) {
        this.id = id;
    }

    // Getter for time
    public int getTime() {
        return time;
    }

    // Setter for time
    public void setTime(int time) {
        this.time = time;
    }

    // Getter for cloudPoints
    public List<List<Double>> getCloudPoints() {
        return cloudPoints;
    }

    // Setter for cloudPoints
    public void setCloudPoints(List<List<Double>> cloudPoints) {
        this.cloudPoints = cloudPoints;
    }

    /**
     * Returns a string representation of the object.
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
