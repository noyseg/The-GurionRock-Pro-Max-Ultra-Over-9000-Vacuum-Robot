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
    private final String name;
    private final int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectsList; // List of detected objects with timestamps

    public Camera(int id, String name, int frequency, List<StampedDetectedObjects> detectedObjectsList ) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
        this.detectedObjectsList = detectedObjectsList;
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

    public String getName(){
        return this.name;
    }

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
         
