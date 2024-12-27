package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id;
    private final int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectsList; // List of detected objects with timestamps
    private int numDetectedObjects;

    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.detectedObjectsList = new LinkedList<StampedDetectedObjects>();
        numDetectedObjects = 0;
    }

    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    // handling in the parse stage
    public void addDetectedObject(StampedDetectedObjects detectedObject) {
        this.detectedObjectsList.add(detectedObject);
    }

    public STATUS getStatus() {
        return status;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setStatus(STATUS error) {
        this.status = error;
    }

    public int getID() {
        return id;
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects;
    }

    public void setNumDetectedObjects(int newNumDetectedObjects) {
        this.numDetectedObjects = newNumDetectedObjects;
    }

}
