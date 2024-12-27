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
    private CloudPoint[] cloudPoint;

    // /**
    //  * Constructs a new TrackedObject.
    //  * @param String      The id of the object
    //  * @param time        The time the object was tracked
    //  * @param description  A description of the object
    //  * @param cloudPoints The array of CloudPoint objects representing the tracked object
    //  * 
    //  */
    // public TrackedObject(String id, int time,String description, CloudPoint[] cloudPoint){
    //     this.id = id;
    //     this.time = time;
    //     this.description = description;
    //     this.cloudPoint[0] = cloudPoint[0];
    //     this.cloudPoint[1] = cloudPoint[1];
    // }

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
    public CloudPoint[] getCloudPoints(){
        return this.cloudPoint;
    }
    
    /**
    * Sets the coordinates of the object
    * 
    * @param cp the coordinates of the object
     */
    public void setCloudPoints(CloudPoint[] cloudPoint){
        this.cloudPoint[0] = cloudPoint[0];
        this.cloudPoint[1] = cloudPoint[1];
    }
    
    @Override
    public String toString() {
        return "TrackedObject{" +
                "time=" + time +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", cloudPoint=" + Arrays.toString(cloudPoint) +
                '}';
    }

}
