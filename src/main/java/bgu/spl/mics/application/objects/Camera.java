package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id; // Unique identifier for the camera.
    private final String name; // The name for the camera.
    private final int frequency; // Camera's frequency 
    private STATUS status; // Current operational status of the camera ((UP, DOWN, ERROR).
    private final List<StampedDetectedObjects> detectedObjectsList; // A list containing detected objects along with timestamps.

        /**
     * Constructs a Camera object.
     *
     * @param id                  The unique identifier for the camera.
     * @param name                The name of the camera.
     * @param frequency           The detection frequency of the camera.
     * @param detectedObjectsList Initial list of detected objects with timestamps.
     */
    public Camera(int id, int frequency, List<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.name = "Camera" + String.valueOf(id);
        this.frequency = frequency;
        this.detectedObjectsList = new LinkedList<>(detectedObjectsList);
        this.status = STATUS.UP; // Cameras start in the UP (active) state.
    }

    /**
     * Retrieves the unique ID of the camera.
     *
     * @return The camera's ID as an integer.
     */
     public int getID() {
        return id;
    }

    /**
     * Retrieves the camera's name.
     *
     * @return A string representing the name of the camera.
     */
    public String getName(){
        return this.name;
    }

    /**
     * Retrieves the current operational status of the camera.
     *
     * @return The camera's status as a `STATUS` enum value (e.g., UP, DOWN, ERROR).
     */
    public STATUS getStatus() {
        return status;
    }

    /**
     * Retrieves the camera's detection frequency.
     *
     * @return An integer representing the frequency of detections (e.g., every N ticks).
     */
    public int getFrequency() {
        return frequency;
    }


    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    /**
     * Updates the camera's operational status.
     *
     * @param status The new status to set (e.g., UP, DOWN, ERROR).
    */
    public void setStatus(STATUS error) {
        this.status = error;
    }

        /**
     * Provides a string representation of the camera object, useful for debugging or logging.
     *
     * @return A string summarizing the camera's key attributes, including ID, name, 
     *         frequency, status, and detected objects.
     */
     @Override
     public String toString() {
         return "Camera{" +
             "id=" + id +
             ", name='" + name + '\'' +
             ", frequency=" + frequency +
             ", status=" + status +
             ", detectedObjectsList=" + detectedObjectsList.toArray().toString() +
             '}';
     }
}
         
