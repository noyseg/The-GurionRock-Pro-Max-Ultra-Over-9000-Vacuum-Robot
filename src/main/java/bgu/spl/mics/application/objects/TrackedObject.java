package bgu.spl.mics.application.objects;

import java.util.Arrays;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {

    private int time; 
    private String id;
    private String description; 
    private CloudPoint[] coordinates;
    
    // Defult constructor
    public TrackedObject(){}

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
    public CloudPoint[] getCoordinates(){
        return this.coordinates;
    }
    
    /**
    * Sets the coordinates of the object
    * 
    * @param cloudPoint the coordinates of the object
     */
    public void setCoordinates(CloudPoint[] cloudPoints){
        this.coordinates = cloudPoints;
    }

    @Override
    public String toString() {
        return "TrackedObject{" +
                "time=" + time +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", coordinates=" + Arrays.toString(coordinates) +
                '}';
    }
}
