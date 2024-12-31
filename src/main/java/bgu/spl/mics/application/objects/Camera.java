package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id;
    private final String type;
    private final String name;
    private final int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectsList; // List of detected objects with timestamps

    public Camera(int id, int frequency) {
        this.id = id;
        this.type = "Camera";
        this.name = this.type + String.valueOf(this.id);
        this.frequency = frequency;
        this.status = STATUS.UP;
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

    public String getType(){
        return this.type;
    }

    public String getName(){
        return this.name;
    }

}
