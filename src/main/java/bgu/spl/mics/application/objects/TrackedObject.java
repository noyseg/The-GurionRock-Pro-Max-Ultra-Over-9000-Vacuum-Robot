package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {

    private final String id;
    private final int time; 
    private final String description; 
    private final List<CloudPoint> coordinates;
    
    /**
     * @param id The ID of the object
     * @param time The time the object was detected
     * @param description The description of the tracked object
     * @param coordinates The coordinates of the object
     */
    public TrackedObject(int time, String id, String description, List<CloudPoint> coordinates) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }

    /**
     * @return The time the object was detected
     */
    public int getTime() {
        return time;
    }

    /**
     * @return The id of the object
     */
    public String getId() {
        return id;
    }

    /**
     * @return The description of the tracked object
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the coordinates of the object
     */
    public List<CloudPoint> getCoordinates(){
        return this.coordinates;
    }

    @Override
    public String toString() {
        return "TrackedObject{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", coordinates=" + coordinates.toString() +
                '}';
    }
}
