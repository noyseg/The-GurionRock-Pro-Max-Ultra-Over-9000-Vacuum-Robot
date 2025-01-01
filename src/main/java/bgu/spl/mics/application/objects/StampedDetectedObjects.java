package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {

    private final int time;
    private final List<DetectedObject> detectedObjects;

    public StampedDetectedObjects() {
        this.time = 0;
        this.detectedObjects = new LinkedList<>();
    }

    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }
    public int getTime() {
        return time;
    }
}
