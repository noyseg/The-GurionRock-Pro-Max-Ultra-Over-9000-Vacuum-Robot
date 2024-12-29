package bgu.spl.mics.application.objects;

import java.util.Arrays;
import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {

    private int time; 
    private String id;
    private String description; 
    private List<CloudPoint> coordinates;
    
    /**
     *
     * 
     * @param time The time the object was tracked
     * @param id The ID of the object
     * @param description The description of the tracked object
     * @param coordinates The coordinates of the object
     */
    public TrackedObject(int time, String id, String description, List<CloudPoint> coordinates) {
        this.time = time;
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
    }

    /**
     * @return The time the object was tracked
     */
    public int getTime() {
        return time;
    }

    /**
    * Sets the time the object was tracked
    * 
    * @param time The time the object was tracked
    */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * @return The id of the object
     */
    public String getId() {
        return id;
    }

    /**
    * Sets the id of the object.
    * 
    * @param id The id to set
    */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * @return The description of the tracked object
     */
    public String getDescription() {
        return description;
    }


    /**
    * Sets the description of the tracked object
    * 
    * @param id The description of the tracked object
    */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the coordinates of the object
     */
    public List<CloudPoint> getCoordinates(){
        return this.coordinates;
    }
    
    /**
    * Sets the coordinates of the object
    * 
    * @param cloudPoint the coordinates of the object
     */
    public void setCoordinates(List<CloudPoint> cloudPoints){
        this.coordinates = cloudPoints;
    }

    @Override
    public String toString() {
        return "TrackedObject{" +
                "time=" + time +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", coordinates=" + coordinates.toString() +
                '}';
    }
}
