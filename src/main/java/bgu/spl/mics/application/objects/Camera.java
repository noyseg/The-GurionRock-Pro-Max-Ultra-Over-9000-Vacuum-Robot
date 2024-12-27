package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id;
    private final int frequency;
    private String name;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectsList; // List of detected objects with timestamps

    public Camera(int id, int frequency, String cameraKey, STATUS status) {
        this.id = id;
        this.frequency = frequency;
        this.name = cameraKey;
        this.status = status;
        this.detectedObjectsList = new ArrayList<>();

    }

    public String getName() {
        return name;
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

}
